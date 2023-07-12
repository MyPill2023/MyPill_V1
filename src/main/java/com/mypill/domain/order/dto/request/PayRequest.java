package com.mypill.domain.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String addressId;
}
