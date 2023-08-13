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
    private Order order;
    private List<OrderItem> orderItems;
    @Builder.Default
    List<OrderStatus> orderStatuses = Arrays.asList(OrderStatus.values());

    public static OrderManagementResponse of(Order order, List<OrderItem> orderItems) {
        return OrderManagementResponse.builder()
                .order(order)
                .orderItems(orderItems)
                .build();
    }
}
