package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import lombok.*;

import java.util.List;

@Builder
@Data
public class CartResponse {

    private Long totalQuantity;
    private Long totalPrice;
    private List<CartProductResponse> cartProducts;

    public static CartResponse of(Cart cart) {
        return CartResponse.builder()
                .totalQuantity(cart.getCartProducts().stream().mapToLong(CartProduct::getQuantity).sum())
                .totalPrice(cart.getCartProducts().stream().mapToLong(cartProduct -> cartProduct.getQuantity() * cartProduct.getProduct().getPrice()).sum())
                .cartProducts(cart.getCartProducts().stream().map(CartProductResponse::of).toList())
                .build();
    }
}