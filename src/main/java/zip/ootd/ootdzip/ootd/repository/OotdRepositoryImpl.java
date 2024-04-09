package zip.ootd.ootdzip.ootd.repository;

import static zip.ootd.ootdzip.clothes.domain.QClothes.*;
import static zip.ootd.ootdzip.ootd.domain.QOotd.*;
import static zip.ootd.ootdzip.ootdimage.domain.QOotdImage.*;
import static zip.ootd.ootdzip.ootdimageclothe.domain.QOotdImageClothes.*;
import static zip.ootd.ootdzip.ootdstyle.domain.QOotdStyle.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.common.response.CommonPageResponse;
import zip.ootd.ootdzip.ootd.data.OotdSearchSortType;
import zip.ootd.ootdzip.ootd.domain.Ootd;
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
                        searchTextCondition(searchText),
                        inBrandIds(brandIds),
                        inCategoryIds(categoryIds),
                        inColorIds(colorIds),
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
                        eqWriterGender(writerGender))
                .fetchOne();

        boolean hasNext = false;
        if (pageSize < findOotds.size()) {
            findOotds.remove(pageSize);
            hasNext = true;
        }

        return new CommonPageResponse<>(findOotds, pageable, !hasNext, totalCount);
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
            orderSpecifiers.add(1, new OrderSpecifier(Order.DESC, ootd.ootdBookmarks.size()));
            orderSpecifiers.add(2, new OrderSpecifier(Order.DESC, ootd.viewCount));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);

    }

}
