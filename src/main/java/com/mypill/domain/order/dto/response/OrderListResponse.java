package com.mypill.domain.order.dto.response;

import com.mypill.domain.address.entity.Address;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderItem;
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
    private LocalDateTime payDate;

    public static OrderListResponse of(Order order){
        return OrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .name(order.getName())
                .totalPrice(order.getTotalPrice())
                .payDate(order.getPayDate())
                .build();
    }

}
