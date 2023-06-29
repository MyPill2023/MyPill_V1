package com.mypill.domain.order.dto.response;

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

    private String name;
    private List<OrderItem> orderItems;
    private Long totalPrice;

    public static OrderResponse of(Order order){
        return OrderResponse.builder()
                .name(order.getName())
                .orderItems(order.getOrderItems())
                .totalPrice(order.getTotalPrice())
                .build();
    }

}
