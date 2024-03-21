package zip.ootd.ootdzip.user.repository;

import static zip.ootd.ootdzip.user.domain.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.user.domain.User;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        super(User.class);
        this.queryFactory = queryFactory;
    }

    public Slice<User> searchUsers(String name, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<User> findUsers = queryFactory.selectFrom(user)
                .where(containName(name))
                .orderBy(
                        user.name.length().asc(),
                        user.name.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (pageSize < findUsers.size()) {
            findUsers.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(findUsers, pageable, hasNext);
    }

    public BooleanExpression containName(String name) {
        return user.name.contains(name);
    }
}
