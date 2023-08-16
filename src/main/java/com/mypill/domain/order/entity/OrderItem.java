package com.mypill.domain.order.entity;

import com.mypill.domain.product.entity.Product;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @NotNull
    private Long totalPrice;
    @NotNull
    private Long price;
    @NotNull
    private Long quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderItem(Product product, Long quantity) {
        this.product = product;
        this.price = product.getPrice();
        this.quantity = quantity;
        this.status = OrderStatus.BEFORE;
        this.totalPrice = this.price * this.quantity;
    }

    public void connectOrder(Order order) {
        this.order = order;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

}
