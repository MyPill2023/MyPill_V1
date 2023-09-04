package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    Optional<Order> findByIdAndPaymentIsNotNull(Long id);

    List<Order> findByBuyerIdAndPaymentIsNotNullOrderByPayment_PayDateDesc(Long buyerId);

    @Modifying
    @Query("delete from Order o where o.createDate <= :cutoffDate and o.primaryOrderStatus = 'BEFORE'")
    void hardDelete(@Param("cutoffDate") LocalDateTime cutoffDate);
}
