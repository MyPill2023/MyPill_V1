package com.mypill.domain.order.dto.response;

import com.mypill.domain.address.dto.response.AddressResponse;
import com.mypill.domain.address.entity.Address;
import com.mypill.domain.order.entity.Order;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.order.entity.Payment;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private String name;
    private String buyerName;
    private List<OrderItemResponse> orderItems;
    private Long totalPrice;
    private AddressResponse deliveryAddress;
    private Payment payment;
    private OrderStatus primaryOrderStatus;

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .name(order.getName())
                .buyerName(order.getBuyer().getName())
                .orderItems(order.getOrderItems().stream().map(OrderItemResponse::of).toList())
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(Optional.ofNullable(order.getDeliveryAddress()).map(AddressResponse::of).orElse(null))
                .payment(order.getPayment())
                .primaryOrderStatus(order.getPrimaryOrderStatus())
                .build();
    }
}
