package com.mypill.domain.order.dto.response;

import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Product product;
    private Long totalPrice;
    private Long quantity;
    private OrderStatus orderStatus;

    public static OrderItemResponse of(OrderItem orderItem){
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .product(orderItem.getProduct())
                .totalPrice(orderItem.getTotalPrice())
                .quantity(orderItem.getQuantity())
                .orderStatus(orderItem.getStatus())
                .build();
    }


}
