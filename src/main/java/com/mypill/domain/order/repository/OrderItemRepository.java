package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {
    List<OrderItem> findByProductSellerIdAndOrderId(Long sellerId, Long orderId);

    @Modifying
    @Query("delete from OrderItem oi where oi.createDate <= :cutoffDate and oi.status = 'BEFORE'")
    void hardDelete(@Param("cutoffDate") LocalDateTime cutoffDate);
}
