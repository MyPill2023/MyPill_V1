package com.mypill.domain.cart.entity;

import com.mypill.domain.product.entity.Product;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private Order order;

    @Column(nullable = false)
    private int quantity;

    public static CartProduct of(Cart cart, Product product, int quantity){
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
