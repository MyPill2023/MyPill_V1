package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {

    private Long id;
    private Member seller;
    private String name;
    private String description;
    private Long price;
    private Long stock;
    private List<Nutrient> nutrients = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Member> likedMembers = new ArrayList<>();
    private boolean isLiked;

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .seller(product.getSeller())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .nutrients(product.getNutrients())
                .categories(product.getCategories())
                .likedMembers(product.getLikedMembers())
                .build();
    }

    public static ProductResponse of(Product product, boolean isLikedInput) {
        return ProductResponse.builder()
                .id(product.getId())
                .seller(product.getSeller())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .nutrients(product.getNutrients())
                .categories(product.getCategories())
                .likedMembers(product.getLikedMembers())
                .isLiked(isLikedInput)
                .build();
    }
}
