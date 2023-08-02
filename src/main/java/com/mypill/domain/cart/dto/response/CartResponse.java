package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

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
        List<CartProduct> filteredProducts = cart.getCartProducts().stream()
                .filter(cartProduct -> cartProduct.getDeleteDate() == null)
                .toList();

        return CartResponse.builder()
                .id(cart.getId())
                .cartProducts(filteredProducts.stream().map(CartProductResponse::of).toList())
                .totalQuantity(filteredProducts.stream().mapToLong(CartProduct::getQuantity).sum())
                .totalPrice(filteredProducts.stream().mapToLong(cartProduct -> cartProduct.getQuantity() * cartProduct.getProduct().getPrice()).sum())
                .build();
    }
}