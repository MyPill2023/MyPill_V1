package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.dto.response.CategoryResponse;
import com.mypill.domain.image.entity.Image;
import com.mypill.domain.nutrient.dto.response.NutrientResponse;
import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long stock;
    @Builder.Default
    private List<NutrientResponse> nutrients = new ArrayList<>();
    @Builder.Default
    private List<CategoryResponse> categories = new ArrayList<>();
    private Long likeCount;
    private Long sellerId;
    private String sellerName;
    private boolean isLiked;
    private String imageFilePath;

    public static ProductResponse createProductResponse(Product product, boolean isLikedInput) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .nutrients(product.getNutrients().stream().map(NutrientResponse::of).toList())
                .categories(product.getCategories().stream().map(CategoryResponse::of).toList())
                .likeCount(product.getLikeCount())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getName())
                .isLiked(isLikedInput)
                .imageFilePath(Optional.ofNullable(product.getImage()).map(Image::getFilepath).orElse("/image-null"))
                .build();
    }

    public static ProductResponse of(Product product) {
        return createProductResponse(product, false);
    }

    public static ProductResponse of(Product product, boolean isLikedInput) {
        return createProductResponse(product, isLikedInput);
    }
}
