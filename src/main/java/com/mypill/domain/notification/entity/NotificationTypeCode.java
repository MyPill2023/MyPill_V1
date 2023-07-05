package com.mypill.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationTypeCode {
    OrderPayment("OrderPayment", "결제 후 주문 생성"),
    OrderStatus("OrderStatus", "주문 상태 변경"),
    Recode("Recode", "복약 기록 알림");

    private String value;
    private String description;


    NotificationTypeCode(String value, String description){
        this.value = value;
        this.description = description;
    }
}
