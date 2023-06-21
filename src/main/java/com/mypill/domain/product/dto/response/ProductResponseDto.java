package com.mypill.domain.product.dto.response;

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


    public static ProductResponseDto of(Product product) {
        return ProductResponseDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .nutrients(product.getNutrients())
                .build();
    }
}
