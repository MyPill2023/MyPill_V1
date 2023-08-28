package com.mypill.domain.order.dto.response;

import com.mypill.domain.order.entity.OrderItem;
import com.mypill.domain.order.entity.OrderStatus;
import com.mypill.domain.product.dto.response.ProductResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private Long totalPrice;
    private Long quantity;
    private OrderStatus orderStatus;

    public static OrderItemResponse of(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .product(ProductResponse.of(orderItem.getProduct()))
                .totalPrice(orderItem.getTotalPrice())
                .quantity(orderItem.getQuantity())
                .orderStatus(orderItem.getStatus())
                .build();
    }


}
