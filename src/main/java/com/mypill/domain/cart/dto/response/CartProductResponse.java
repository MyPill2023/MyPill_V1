package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.product.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CartProductResponse {
    private Long id;
    private Product product;
    private Long quantity;
    private LocalDateTime deleteDate;

    public static CartProductResponse of(CartProduct cartProduct) {
        return CartProductResponse.builder()
                .id(cartProduct.getId())
                .product(cartProduct.getProduct())
                .quantity(cartProduct.getQuantity())
                .build();
    }
}