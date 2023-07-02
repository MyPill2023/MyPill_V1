package com.mypill.domain.order.repository;

import com.mypill.domain.member.entity.QMember;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.QOrder;
import com.mypill.domain.order.entity.QOrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mypill.domain.order.entity.QOrder.order;
import static com.mypill.domain.order.entity.QOrderItem.orderItem;
import static com.mypill.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<Order> findBySellerId(Long sellerId) {
        return jpaQueryFactory.selectFrom(order)
                .join(order.orderItems, orderItem)
                .join(orderItem.product, product)
                .where(product.seller.id.eq(sellerId)
                        .and(order.payDate.isNotNull()))
                .fetch();
    }
}
