package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import lombok.*;

import java.util.List;

@Builder
@Data
public class CartResponse {
    private Long id;
    private List<CartProduct> cartProducts;
    private Long totalQuantity;
    private Long totalPrice;

    public static CartResponse of(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .cartProducts(cart.getCartProducts())
                .totalQuantity(cart.getCartProducts().stream().mapToLong(CartProduct::getQuantity).sum())
                .totalPrice(cart.getCartProducts().stream().mapToLong(cartProduct -> cartProduct.getQuantity() * cartProduct.getProduct().getPrice()).sum())
                .build();
    }
}