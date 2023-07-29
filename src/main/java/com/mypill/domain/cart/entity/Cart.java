package com.mypill.domain.cart.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartProduct> cartProducts = new ArrayList<>();

    @Formula("(SELECT COALESCE(SUM(cp.quantity), 0) FROM cart_product cp WHERE cp.cart_id = id AND cp.delete_date IS NULL)")
    private Long totalQuantity;

    @Formula("(SELECT COALESCE(SUM(cp.quantity * p.price), 0) FROM cart_product cp INNER JOIN product p ON cp.product_id = p.id WHERE cp.cart_id = id AND cp.delete_date IS NULL)")
    private Long totalPrice;

    public static Cart createCart(Member member) {
        return Cart.builder()
                .member(member)
                .build();
    }

    public void addCartProduct(CartProduct cartProduct) {
        this.cartProducts.add(cartProduct);
    }
}