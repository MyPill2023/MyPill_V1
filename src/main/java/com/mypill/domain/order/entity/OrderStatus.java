package com.mypill.domain.order.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    BEFORE("주문 전", 0),
    ORDERED("결제 완료", 1),
    PREPARING("상품 준비중", 2),
    SHIPPING("배송 중", 3),
    DELIVERED("배송 완료", 4),
    CANCELED("주문 취소", 5);
    private final String value;
    private final int priority;

    OrderStatus(String value, int priority) {
        this.value = value;
        this.priority = priority;
    }

    public static OrderStatus findByValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    public static OrderStatus[] getManagementStatus() {
        return new OrderStatus[]{ORDERED, PREPARING, SHIPPING, DELIVERED};
    }
}
