package com.mypill.domain.cart.dto.response;

import com.mypill.domain.cart.entity.Cart;
import com.mypill.domain.cart.entity.CartProduct;
import com.mypill.domain.image.entity.Image;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Builder
@Data
public class CartProductResponse {
    private Long price;
    private Long id;
    private Long productId;
    private Long quantity;
    private String imageFilePath;
    private String sellerName;
    private String productName;

    public static CartProductResponse of(CartProduct cartProduct) {
        return CartProductResponse.builder()
                .price(cartProduct.getProduct().getPrice())
                .id(cartProduct.getId())
                .productId(cartProduct.getProduct().getId())
                .quantity(cartProduct.getQuantity())
                .imageFilePath(Optional.ofNullable(cartProduct.getProduct().getImage()).map(Image::getFilepath).orElse("/image-null"))
                .sellerName(cartProduct.getProduct().getSeller().getName())
                .productName(cartProduct.getProduct().getName())
                .build();
    }
}
