package com.mypill.domain.product.entity;

import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequestDto;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Long stock;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Nutrient> nutrients = new ArrayList<>();

    //이미지

    public static Product of(ProductRequestDto requestDto) {
        return Product.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .nutrients(requestDto.getNutrients())
                .build();
    }

//    public void update(ProductRequestDto request){
//        this.name = request.getName();
//        this.description = request.getDescription();
//        this.price = request.getPrice();
//        this.stock = request.getStock();
//        this.nutrients = request.getNutrients();
//    }
}
