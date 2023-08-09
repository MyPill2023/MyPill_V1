package com.mypill.domain.product.dto.response;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.nutrient.entity.Nutrient;
import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductPageResponse {
    String title;
    Page<Product> productPage;
    List<Nutrient> nutrients;
    List<Category> categories;
    String pageUrl;

    public static ProductPageResponse of(String title, Page<Product> productPage, List<Nutrient> nutrients, List<Category> categories, String pageUrl) {
        return new ProductPageResponse(title, productPage, nutrients, categories, pageUrl);
    }
}
