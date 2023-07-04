package com.mypill.domain.order.dto.response;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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

    public static OrderListResponse of(Order order){
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
