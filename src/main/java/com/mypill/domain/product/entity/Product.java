package com.mypill.domain.product.entity;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.dto.request.ProductRequest;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_nutrient", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "nutrient_id")  // Nutrient 기본키
    )
    private List<Nutrient> nutrients = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_category", // 연결테이블
            joinColumns = @JoinColumn(name = "product_id"),  // Product 기본키
            inverseJoinColumns = @JoinColumn(name = "category_id")  // Category 기본키
    )
    private List<Category> categories = new ArrayList<>();

    //이미지

    public static Product of(ProductRequest requestDto) {
        return Product.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .nutrients(requestDto.getNutrients())
                .categories(requestDto.getCategories())
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
