package com.mypill.domain.order.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    public String method;
    private Long totalAmount;
    private String status;
    private LocalDateTime payDate;
}
