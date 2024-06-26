package zip.ootd.ootdzip.user.repository;

import static zip.ootd.ootdzip.user.domain.QUser.*;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.user.data.UserSearchType;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        super(User.class);
        this.queryFactory = queryFactory;
    }

    public Page<User> searchUsers(
            UserSearchType searchType,
            String name,
            Long userId,
            Set<Long> nonAccessibleUserIds,
            Pageable pageable) {

        BooleanExpression additionalCondition = switch (searchType) {
            case FOLLOWER -> containUserInFollowings(userId);
            case FOLLOWING -> containUserInFollowers(userId);
            default -> null;
        };

        List<User> findUsers = queryFactory.selectFrom(user)
                .where(
                        additionalCondition,
                        containName(name),
                        user.isDeleted.eq(false),
                        user.isCompleted.eq(true),
                        notInNonAccessibleUserIds(nonAccessibleUserIds)
                )
                .orderBy(
                        user.name.length().asc(),
                        user.name.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(user.count())
                .from(user)
                .where(
                        additionalCondition,
                        containName(name),
                        user.isDeleted.eq(false),
                        user.isCompleted.eq(true),
                        notInNonAccessibleUserIds(nonAccessibleUserIds)
                )
                .fetchOne();

        return new PageImpl<>(findUsers, pageable, totalCount);
    }

    private BooleanExpression containUserInFollowings(Long userId) {
        // 전체유저 팔로잉에서 본인이 포함되면 반환 => 나를 팔로우한 사람을 가져옴
        return user.followings.any().id.eq(userId);
    }

    private BooleanExpression containUserInFollowers(Long userId) {
        // 전체유저 팔로워에서 본인을 포함하면 반환 => 내가 팔로우한 사람을 가져옴
        return user.followers.any().id.eq(userId);
    }

    public BooleanExpression containName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        return user.name.contains(name);
    }

    public BooleanExpression notInNonAccessibleUserIds(Set<Long> nonAccessibleUserIds) {
        if (nonAccessibleUserIds == null
                || (1 == nonAccessibleUserIds.size()
                && nonAccessibleUserIds.contains(0L))) {
            return null;
        }

        return user.id.notIn(nonAccessibleUserIds);
    }
}
