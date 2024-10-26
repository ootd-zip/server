package zip.ootd.ootdzip.brandrequest.repository;

import static zip.ootd.ootdzip.brandrequest.domain.QBrandRequest.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import zip.ootd.ootdzip.brandrequest.data.BrandRequestStatus;
import zip.ootd.ootdzip.brandrequest.domain.BrandRequest;
import zip.ootd.ootdzip.brandrequest.repository.model.BrandRequestSearchRepoParam;

@Repository
public class BrandRequestRepositoryImpl extends QuerydslRepositorySupport implements BrandRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BrandRequestRepositoryImpl(JPAQueryFactory queryFactory) {
        super(BrandRequest.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<BrandRequest> searchBrandRequests(BrandRequestSearchRepoParam param, Pageable pageable) {
        List<BrandRequest> contents = queryFactory.selectFrom(brandRequest)
                .where(searchTextCondition(param.getSearchText()),
                        brandRequestStatusEq(param.getBrandRequestStatus()))
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(brandRequest.count())
                .from(brandRequest)
                .where(searchTextCondition(param.getSearchText()),
                        brandRequestStatusEq(param.getBrandRequestStatus()))
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> totalCount);
    }

    private BooleanExpression searchTextCondition(String searchText) {
        return searchText == null ? null
                : brandRequest.requestContents.contains(searchText)
                .or(brandRequest.requestUser.name.contains(searchText));

    }

    private BooleanExpression brandRequestStatusEq(BrandRequestStatus status) {
        return status == null ? null : brandRequest.requestStatus.eq(status);
    }

    private OrderSpecifier[] getOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            PathBuilder orderByExpression = new PathBuilder(BrandRequest.class, "brandRequest");
            orders.add(new OrderSpecifier(direction, orderByExpression.get(property)));
        });

        return orders.stream().toArray(OrderSpecifier[]::new);
    }
}
