package com.mypill.domain.product.dto.request;

import com.mypill.domain.nutrient.entity.Nutrient;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequestDto {
    @NotBlank(message = "제품 이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "제품의 상세설명을 입력해주세요.")
    private String description;
    @NotBlank(message = "제품 가격을 입력해주세요.")
    private Long price;
    @NotBlank(message = "제품의 재고를 입력해주세요.")
    private Long stock;
    @NotBlank(message = "제품의 성분을 선택해주세요.")
    private List<Nutrient> nutrients;
}
