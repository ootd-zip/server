package zip.ootd.ootdzip.ootd.repository;

import static zip.ootd.ootdzip.category.domain.QCategory.*;
import static zip.ootd.ootdzip.category.domain.QCategoryTemperature.*;
import static zip.ootd.ootdzip.category.domain.QColor.*;
import static zip.ootd.ootdzip.clothes.domain.QClothes.*;
import static zip.ootd.ootdzip.clothes.domain.QClothesColor.*;
import static zip.ootd.ootdzip.ootd.domain.QOotd.*;
import static zip.ootd.ootdzip.ootdimage.domain.QOotdImage.*;
import static zip.ootd.ootdzip.ootdimageclothe.domain.QOotdImageClothes.*;
import static zip.ootd.ootdzip.ootdstyle.domain.QOotdStyle.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.domain.UserGender;

@Repository
public class OotdRepositoryImpl extends QuerydslRepositorySupport implements OotdRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OotdRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Ootd.class);
        this.queryFactory = queryFactory;
    }

    public CommonPageResponse<Ootd> searchOotds(String searchText,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            UserGender writerGender,
            Set<Long> nonAccessibleUserIds,
            OotdSearchSortType sortType,
            Pageable pageable) {

        int pageSize = pageable.getPageSize();

        List<Ootd> findOotds = queryFactory.selectFrom(ootd)
                .distinct()
                .innerJoin(ootd.styles, ootdStyle)
                .innerJoin(ootd.ootdImages, ootdImage)
                .leftJoin(ootdImage.ootdImageClothesList, ootdImageClothes)
                .leftJoin(ootdImageClothes.clothes, clothes)
                .where(
                        ootd.isPrivate.eq(false),
                        ootd.writer.isDeleted.eq(false),
                        searchTextCondition(searchText),
                        inBrandIds(brandIds),
                        inCategoryIds(categoryIds),
                        inColorIds(colorIds),
                        notInUserIds(nonAccessibleUserIds),
                        eqWriterGender(writerGender))
                .orderBy(createOrderSpecifiers(sortType))
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        Long totalCount = queryFactory.select(ootd.countDistinct())
                .from(ootd)
                .innerJoin(ootd.styles, ootdStyle)
                .innerJoin(ootd.ootdImages, ootdImage)
                .leftJoin(ootdImage.ootdImageClothesList, ootdImageClothes)
                .leftJoin(ootdImageClothes.clothes, clothes)
                .where(
                        ootd.isPrivate.eq(false),
                        searchTextCondition(searchText),
                        inBrandIds(brandIds),
                        inCategoryIds(categoryIds),
                        inColorIds(colorIds),
                        notInUserIds(nonAccessibleUserIds),
                        eqWriterGender(writerGender))
                .fetchOne();

        boolean hasNext = false;
        if (pageSize < findOotds.size()) {
            findOotds.remove(pageSize);
            hasNext = true;
        }

        return new CommonPageResponse<>(findOotds, pageable, !hasNext, totalCount);
    }

    private BooleanExpression notInUserIds(Set<Long> nonAccessibleUserIds) {
        if (nonAccessibleUserIds == null
                || (1 == nonAccessibleUserIds.size()
                && nonAccessibleUserIds.contains(0L))) {
            return null;
        }

        return ootd.writer.id.notIn(nonAccessibleUserIds);
    }

    private BooleanExpression searchTextCondition(String searchText) {
        if (searchText == null
                || searchText.isBlank()) {
            return null;
        }

        return ootd.contents.contains(searchText)
                .or(ootd.writer.name.contains(searchText))
                .or(ootdStyle.style.name.eq(searchText))
                .or(clothes.brand.name.contains(searchText));
    }

    private BooleanExpression inBrandIds(List<Long> brandIds) {
        if (brandIds == null
                || brandIds.isEmpty()) {
            return null;
        }
        return clothes.brand.id.in(brandIds);
    }

    private BooleanExpression inCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null
                || categoryIds.isEmpty()) {
            return null;
        }
        return clothes.category.id.in(categoryIds)
                .or(clothes.category.parentCategory.id.in(categoryIds));
    }

    private BooleanExpression inColorIds(List<Long> colorIds) {
        if (colorIds == null
                || colorIds.isEmpty()) {
            return null;
        }
        return clothes.clothesColors.any().color.id.in(colorIds);
    }

    private BooleanExpression eqWriterGender(UserGender gender) {
        if (gender == null) {
            return null;
        }

        return ootd.writer.gender.eq(gender);
    }

    private OrderSpecifier[] createOrderSpecifiers(OotdSearchSortType sortType) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        orderSpecifiers.add(new OrderSpecifier(Order.DESC, ootd.createdAt));
        if (sortType.equals(OotdSearchSortType.POPULARITY)) {
            orderSpecifiers.add(0, new OrderSpecifier(Order.DESC, ootd.likeCount));
            orderSpecifiers.add(1, new OrderSpecifier(Order.DESC, ootd.bookmarkCount));
            orderSpecifiers.add(2, new OrderSpecifier(Order.DESC, ootd.viewCount));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);

    }

    public List<Ootd> findOotdToday(double highestTemp, double lowestTemp, User user) {
        int minClothesCount = 1;

        List<Ootd> result = queryFactory.selectDistinct(ootd)
                .from(ootd)
                .join(ootd.ootdImages, ootdImage)
                .join(ootdImage.ootdImageClothesList, ootdImageClothes)
                .join(ootdImageClothes.clothes, clothes)
                .join(clothes.category, category)
                .join(clothes.clothesColors, clothesColor)
                .join(clothesColor.color, color)
                .join(category.temperature, categoryTemperature)
                .where(
                        ootd.writer.id.ne(user.getId())
                                .and(ootd.isPrivate.isFalse())
                                .and(categoryTemperature.lowestTemperature.goe(lowestTemp))
                                .and(categoryTemperature.highestTemperature.loe(highestTemp))
                )
                .groupBy(ootd)
                .having(clothes.count().goe(minClothesCount))
                .limit(10)
                .fetch();

        return result;
    }

    public Map<Clothes, Set<Clothes>> findMatchingUserClothes(List<Clothes> clothesList, User user) {
        Map<Long, Map<Long, List<Clothes>>> categoryColorMap = new HashMap<>();
        for (Clothes clothes : clothesList) {
            Long categoryId = clothes.getCategory().getId();
            Map<Long, List<Clothes>> ColorMap = categoryColorMap.computeIfAbsent(categoryId, k -> new HashMap<>());
            List<Long> colorIds = clothes.getClothesColors().stream().map(cc -> cc.getColor().getId()).toList();
            for (Long colorId : colorIds) {
                List<Clothes> mapClothesList = ColorMap.computeIfAbsent(colorId, k -> new ArrayList<>());
                mapClothesList.add(clothes);
            }
        }

        List<Clothes> userClothes = queryFactory
                .selectFrom(clothes)
                .where(clothes.user.eq(user))
                .fetch();

        Map<Clothes, Set<Clothes>> result = new HashMap<>();
        for (Clothes clothes : userClothes) {
            Map<Long, List<Clothes>> colorMap = categoryColorMap.get(clothes.getCategory().getId());
            List<Long> colorIds = clothes.getClothesColors().stream().map(cc -> cc.getColor().getId()).toList();
            for (Long colorId : colorIds) {
                List<Clothes> taggedClothesList = colorMap.get(colorId);
                for (Clothes taggedClothes : taggedClothesList) {
                    result.computeIfAbsent(taggedClothes, k -> new HashSet<>()).add(clothes);
                }
            }
        }

        return result;
    }
}
