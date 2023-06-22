package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long stock;
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    public static ProductResponseDto of(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .nutrients(product.getNutrients())
                .categories(product.getCategories())
                .build();
    }
}
