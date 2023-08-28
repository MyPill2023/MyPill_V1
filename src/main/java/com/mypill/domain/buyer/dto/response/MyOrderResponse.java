package com.mypill.domain.buyer.dto.response;


import com.mypill.domain.order.dto.response.OrderResponse;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MyOrderResponse {
    private List<OrderResponse> orders;
    private Map<OrderStatus, Long> orderStatusCount;
    @Builder.Default
    private OrderStatus[] filteredOrderStatus = OrderStatus.getManagementStatus();

    public static MyOrderResponse of(List<Order> orders, Map<OrderStatus, Long> orderStatusCount) {
        return MyOrderResponse.builder()
                .orders(orders.stream().map(OrderResponse::of).toList())
                .orderStatusCount(orderStatusCount)
                .build();
    }

}
