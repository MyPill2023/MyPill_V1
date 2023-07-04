package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.OrderItem;

import java.util.List;

public interface OrderItemRepositoryCustom {
    List<OrderItem> findBySellerId(Long sellerId);
    List<OrderItem> findByBuyerId(Long buyerId);
}
