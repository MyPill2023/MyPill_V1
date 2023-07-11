package com.mypill.domain.order.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    private String paymentKey;
    private String method;
    private Long totalAmount;
    private String status;
    private LocalDateTime payDate;
    private LocalDateTime cancelDate;

    public Payment(String paymentKey, String method, Long totalAmount, LocalDateTime payDate, String status) {
        this.paymentKey = paymentKey;
        this.method = method;
        this.totalAmount = totalAmount;
        this.payDate = payDate;
        this.status = status;
    }

    public void updateCancelData(LocalDateTime cancelDate, String status) {
        this.cancelDate = cancelDate;
        this.status = status;
    }
}
