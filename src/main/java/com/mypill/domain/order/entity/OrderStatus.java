package com.mypill.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    BEFORE("BEFORE"),
    ORDERED("ORDERED"),
    PREPARING("PREPARING"),
    SHIPPING("SHIPPING"),
    DELIVERED("DELIVERED"),
    CANCELED("CANCELED");

    private String value;

    OrderStatus(String value) {
        this.value = value;
    }
}