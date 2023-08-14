package com.mypill.domain.cart.entity;

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
public class CartProduct extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    private Long orderId;

    @NotNull
    private Long quantity;

    public static CartProduct of(Cart cart, Product product, Long quantity) {
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void connectOrderId(Long orderId) {
        this.orderId = orderId;
    }

}