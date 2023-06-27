package com.mypill.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartProductRequest {
    private Long ProductId;
    @Min(value=1, message = "수량은 1개 이상이어야 합니다.")
    private int quantity;

}
