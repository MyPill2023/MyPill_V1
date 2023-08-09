package com.mypill.domain.product.dto.response;

import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {

    private Product product;
    private boolean isLiked;

    public ProductResponse(Product product) {
        this.product = product;
    }

    public static ProductResponse of(Product product) {
        return new ProductResponse(product);
    }

    public static ProductResponse of(Product product, boolean isLikedInput) {
        return new ProductResponse(product, isLikedInput);
    }
}
