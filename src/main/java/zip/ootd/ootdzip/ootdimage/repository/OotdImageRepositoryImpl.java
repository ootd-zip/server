package zip.ootd.ootdzip.ootdimage.repository;

import static zip.ootd.ootdzip.clothes.domain.QClothes.*;
import static zip.ootd.ootdzip.clothes.domain.QClothesColor.*;
import static zip.ootd.ootdzip.ootd.domain.QOotd.*;
import static zip.ootd.ootdzip.ootdimage.domain.QOotdImage.*;
import static zip.ootd.ootdzip.ootdimageclothe.domain.QOotdImageClothes.*;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.category.domain.Category;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootdimage.domain.OotdImage;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public class OotdImageRepositoryImpl extends QuerydslRepositorySupport implements OotdImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OotdImageRepositoryImpl(JPAQueryFactory queryFactory) {
        super(OotdImage.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Ootd> findOotdsFromOotdImageForSCDF(List<Long> colorIds,
            Category category,
            User user,
            Set<Long> nonAccessibleUserIds,
            Pageable pageable) {
        return queryFactory.select(ootd)
                .distinct()
                .from(ootdImage)
                .innerJoin(ootdImage.ootd, ootd)
                .innerJoin(ootdImage.ootdImageClothesList, ootdImageClothes)
                .innerJoin(ootdImageClothes.clothes, clothes)
                .innerJoin(clothes.clothesColors, clothesColor)
                .where(
                        clothes.category.eq(category),
                        ootd.writer.ne(user),
                        clothesColor.color.id.in(colorIds),
                        clothes.isPrivate.eq(false),
                        ootd.isPrivate.eq(false),
                        ootd.writer.isDeleted.eq(false),
                        notInUserIds(nonAccessibleUserIds)
                )
                .orderBy(
                        ootd.likeCount.desc(),
                        ootd.bookmarkCount.desc(),
                        ootd.viewCount.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression notInUserIds(Set<Long> nonAccessibleUserIds) {
        if (nonAccessibleUserIds == null
                || (1 == nonAccessibleUserIds.size()
                && nonAccessibleUserIds.contains(0L))) {
            return null;
        }

        return ootd.writer.id.notIn(nonAccessibleUserIds);
    }
}
