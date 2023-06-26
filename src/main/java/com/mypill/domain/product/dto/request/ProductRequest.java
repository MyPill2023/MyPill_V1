package com.mypill.domain.product.dto.request;

import com.mypill.domain.category.entity.Category;
import com.mypill.domain.member.entity.Member;
import com.mypill.domain.nutrient.entity.Nutrient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductRequest {
    private Long sellerId;
    @NotBlank(message = "제품 이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "제품의 상세설명을 입력해주세요.")
    private String description;
    @NotNull(message = "제품 가격을 입력해주세요.")
    private Long price;
    @NotNull(message = "제품의 재고를 입력해주세요.")
    private Long stock;
    @NotNull(message = "제품의 성분을 선택해주세요.")
    private List<Nutrient> nutrients;
    @NotNull(message = "제품의 주요기능을 선택해주세요.")
    private List<Category> categories;
}
