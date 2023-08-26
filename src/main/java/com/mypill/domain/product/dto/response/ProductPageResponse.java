package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.dto.response.CategoryResponse;
import com.mypill.domain.category.entity.Category;
import com.mypill.domain.nutrient.dto.response.NutrientResponse;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductPageResponse {
    String title;
    Page<ProductResponse> productPage;
    List<NutrientResponse> nutrients;
    List<CategoryResponse> categories;
    String pageUrl;

    public static ProductPageResponse of(String title, Page<Product> productPage, List<Nutrient> nutrients, List<Category> categories, String pageUrl) {
        return ProductPageResponse.builder()
                .title(title)
                .productPage(productPage.map(ProductResponse::of))
                .nutrients(nutrients.stream().map(NutrientResponse::of).toList())
                .categories(categories.stream().map(CategoryResponse::of).toList())
                .pageUrl(pageUrl)
                .build();
    }
}
