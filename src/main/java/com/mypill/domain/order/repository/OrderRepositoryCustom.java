package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {

    List<Order> findBySellerId(Long sellerId);
}
