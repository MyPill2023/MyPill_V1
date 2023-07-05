package com.mypill.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationTypeCode {
    OrderStatus("주문 상태 변경"),
    Recode("복약 기록 알림");

    private String value;

    NotificationTypeCode(String value){
        this.value = value;
    }
}
