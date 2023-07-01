package com.mypill.domain.order.dto.response;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private String name;
    private String buyerName;
    private List<OrderItem> orderItems;
    private Long totalPrice;
    private Address deliveryAddress;

    public static OrderResponse of(Order order){
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .name(order.getName())
                .buyerName(order.getBuyer().getName())
                .orderItems(order.getOrderItems())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .build();
    }

}
