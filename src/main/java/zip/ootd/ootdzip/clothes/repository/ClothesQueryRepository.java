package zip.ootd.ootdzip.clothes.repository;

import static zip.ootd.ootdzip.clothes.domain.QClothes.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.clothes.controller.response.FindClothesByUserRes;
import zip.ootd.ootdzip.clothes.domain.Clothes;

@Repository
public class ClothesQueryRepository extends QuerydslRepositorySupport {

    @Autowired
    private JPAQueryFactory queryFactory;

    public ClothesQueryRepository() {
        super(Clothes.class);
    }

    public Slice<FindClothesByUserRes> findClothesByUser(Long userId,
            Boolean isPrivate,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> colorIds,
            Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<FindClothesByUserRes> findClothes = queryFactory.select(Projections.constructor(FindClothesByUserRes.class,
                        clothes.id,
                        clothes.imageUrl))
                .from(clothes)
                .where(eqUserId(userId),
                        eqIsPrivate(isPrivate),
                        inBrandIds(brandIds),
                        inCategoryIds(categoryIds),
                        inColorIds(colorIds))
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (pageSize < findClothes.size()) {
            findClothes.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(findClothes, pageable, hasNext);
    }

    private BooleanExpression eqUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return clothes.user.id.eq(userId);
    }

    private BooleanExpression eqIsPrivate(Boolean isPrivate) {
        if (isPrivate == null) {
            return null;
        }
        return clothes.isPrivate.eq(isPrivate);
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

}
