package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {

    List<OrderItem> findByProductSellerIdAndOrderId(Long sellerId, Long orderId);

}