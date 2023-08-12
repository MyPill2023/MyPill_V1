package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    @Query("select oi FROM OrderItem oi JOIN oi.product p WHERE p.seller.id = :sellerId AND oi.order.id = :orderId")
    List<OrderItem> findByProductSellerIdAndOrderId(Long sellerId, Long orderId);
}
