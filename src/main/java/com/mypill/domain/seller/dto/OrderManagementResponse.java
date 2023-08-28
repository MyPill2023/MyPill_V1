package com.mypill.domain.seller.dto;

import com.mypill.domain.order.dto.response.OrderItemResponse;
import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderManagementResponse {
    private List<OrderResponse> orders;
    private List<OrderItemResponse> orderItems;
    private Map<OrderStatus, Long> orderStatusCount;
    @Builder.Default
    private OrderStatus[] filteredOrderStatus = OrderStatus.getManagementStatus();

    public static OrderManagementResponse of(List<Order> orders, List<OrderItem> orderItems, Map<OrderStatus, Long> orderStatusCount) {
        return OrderManagementResponse.builder()
                .orders(orders.stream().map(OrderResponse::of).toList())
                .orderItems(orderItems.stream().map(OrderItemResponse::of).toList())
                .orderStatusCount(orderStatusCount)
                .build();
    }
}
