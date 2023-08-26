package com.mypill.domain.order.dto.response;

import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderManagementResponse {
    private OrderResponse order;
    private List<OrderItemResponse> orderItems;
    @Builder.Default
    List<OrderStatus> orderStatuses = Arrays.asList(OrderStatus.values());

    public static OrderManagementResponse of(Order order, List<OrderItem> orderItems) {
        return OrderManagementResponse.builder()
                .order(OrderResponse.of(order))
                .orderItems(orderItems.stream().map(OrderItemResponse::of).toList())
                .build();
    }
}
