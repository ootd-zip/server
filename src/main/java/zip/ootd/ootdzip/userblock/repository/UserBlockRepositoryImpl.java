package zip.ootd.ootdzip.userblock.repository;

import static zip.ootd.ootdzip.userblock.domain.QUserBlock.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.userblock.domain.UserBlock;

@Repository
public class UserBlockRepositoryImpl extends QuerydslRepositorySupport implements UserBlockRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public UserBlockRepositoryImpl(JPAQueryFactory queryFactory) {
        super(UserBlock.class);
        this.queryFactory = queryFactory;
    }

    public Boolean existUserBlock(Long userId1, Long userId2) {
        Integer fetchOne = queryFactory.selectOne()
                .from(userBlock)
                .where(
                        eqBlockUserIdAndBlockedUserId(userId1, userId2)
                                .or(eqBlockUserIdAndBlockedUserId(userId2, userId1))
                ).fetchOne();

        return fetchOne != null;
    }

    public Set<Long> getNonAccessibleUserIds(Long accessUserId) {
        List<Long> blockUserIds = queryFactory.select(userBlock.blockUser.id)
                .from(userBlock)
                .where(userBlock.blockedUser.id.eq(accessUserId))
                .fetch();

        List<Long> blockedUserIds = queryFactory.select(userBlock.blockedUser.id)
                .from(userBlock)
                .where(userBlock.blockUser.id.eq(accessUserId))
                .fetch();

        HashSet<Long> result = new HashSet<>(blockUserIds);
        result.addAll(blockedUserIds);

        if (result.isEmpty()) {
            result.add(0L);
        }

        return result;
    }

    private BooleanExpression eqBlockUserIdAndBlockedUserId(Long blockUserId, Long blockedUserId) {
        return userBlock.blockUser.id.eq(blockUserId).and(userBlock.blockedUser.id.eq(blockedUserId));
    }

}
