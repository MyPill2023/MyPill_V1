package com.mypill.domain.order.dto.response;

import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class OrderListResponse {

    private Long orderId;
    private String orderNumber;
    private String name;
    private Long totalPrice;
    private Payment payment;
    private OrderStatus primaryOrderStatus;

    public static OrderListResponse of(Order order) {
        return OrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .name(order.getName())
                .totalPrice(order.getTotalPrice())
                .payment(order.getPayment())
                .primaryOrderStatus(order.getPrimaryOrderStatus())
                .build();
    }

}
