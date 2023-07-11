package com.mypill.domain.order.repository;

import com.mypill.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    List<Order> findByBuyerId(Long buyerId);

    Optional<Order> findByIdAndPaymentIsNotNull(Long id);

    List<Order> findByBuyerIdAndPaymentIsNotNull(Long buyerId);

}
