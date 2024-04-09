package zip.ootd.ootdzip.brand.repository;

import static zip.ootd.ootdzip.brand.domain.QBrand.*;
import static zip.ootd.ootdzip.clothes.domain.QClothes.*;
import static zip.ootd.ootdzip.user.domain.QUser.*;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.brand.domain.Brand;

@Repository
public class BrandRepositoryImpl extends QuerydslRepositorySupport implements BrandRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BrandRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Brand.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Brand> getUserBrands(Long userId, Boolean isPrivate) {
        return queryFactory.select(brand)
                .distinct()
                .from(clothes)
                .innerJoin(clothes.brand, brand)
                .where(user.id.eq(userId),
                        user.isDeleted.eq(false),
                        eqIsPrivate(isPrivate))
                .orderBy(brand.name.asc())
                .fetch();
    }

    public BooleanExpression eqIsPrivate(Boolean isPrivate) {
        if (isPrivate == null) {
            return null;
        }

        return clothes.isPrivate.eq(isPrivate);
    }
}
