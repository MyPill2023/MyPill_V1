package com.mypill.domain.product.dto.response;

import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductsResponse {

    private List<ProductResponse> products;

    public static ProductsResponse of(List<Product> products) {
        return new ProductsResponse(products.stream().map(ProductResponse::of).toList());
    }
}
