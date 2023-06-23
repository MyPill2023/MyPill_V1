package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListResponse {
    private Long id;
    private String name;
    private Long price;
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .nutrients(product.getNutrients())
                .categories(product.getCategories())
                .build();
    }
}
