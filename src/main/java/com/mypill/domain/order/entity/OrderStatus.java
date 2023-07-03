package com.mypill.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    BEFORE("주문 전"),
    ORDERED("결제 완료"),
    PREPARING("상품 준비중"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("주문 취소");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public static OrderStatus findByValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}