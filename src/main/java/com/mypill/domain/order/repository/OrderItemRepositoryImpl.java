package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mypill.domain.order.entity.QOrder.order;
import static com.mypill.domain.order.entity.QOrderItem.orderItem;
import static com.mypill.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<OrderItem> findBySellerId(Long sellerId){
        return jpaQueryFactory.selectFrom(orderItem)
                .join(orderItem.product, product)
                .where(product.seller.id.eq(sellerId)
                        .and(orderItem.status.ne(OrderStatus.BEFORE)))
                .fetch();
    }

    @Override
    public List<OrderItem> findByBuyerId(Long buyerId){
        return jpaQueryFactory.selectFrom(orderItem)
                .where(orderItem.order.buyer.id.eq(buyerId)
                        .and(orderItem.status.ne(OrderStatus.BEFORE)))
                .fetch();
    }
}
