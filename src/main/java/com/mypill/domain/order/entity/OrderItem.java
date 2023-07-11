package com.mypill.domain.order.entity;

import com.mypill.domain.product.entity.Product;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Order order;
    @ManyToOne(fetch = LAZY)
    private Product product;
    @Column(nullable = false)
    private Long totalPrice;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderItem(Product product, Long quantity){
        this.product = product;
        this.price = product.getPrice();
        this.quantity = quantity;
        this.totalPrice = this.price * this.quantity;
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void updateStatus(OrderStatus status){
        this.status = status;
    }

    public void setPaymentDone() {
        status = OrderStatus.ORDERED;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }
}
