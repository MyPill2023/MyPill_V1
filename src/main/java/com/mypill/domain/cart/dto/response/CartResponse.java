package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private List<CartProductResponse> cartProducts;
    private Long totalQuantity;
    private Long totalPrice;

    public static CartResponse of(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .cartProducts(cart.getCartProducts().stream()
                        .filter(cartProduct -> cartProduct.getDeleteDate() == null)
                        .map(CartProductResponse::of)
                        .collect(Collectors.toList()))
                .totalQuantity(cart.getTotalQuantity())
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}