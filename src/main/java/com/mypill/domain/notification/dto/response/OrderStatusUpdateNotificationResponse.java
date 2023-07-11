package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderStatusUpdateNotificationResponse extends NotificationResponse {

    private Long orderId;
    private OrderStatus newStatus;
    private String productName;

    public static OrderStatusUpdateNotificationResponse of(Notification notification) {
        return OrderStatusUpdateNotificationResponse.builder()
                .id(notification.getId())
                .typeCode(notification.getTypeCode())
                .createDate(notification.getCreateDate())
                .orderId(notification.getOrderItem().getOrder().getId())
                .newStatus(notification.getNewStatus())
                .productName(notification.getOrderItem().getProduct().getName())
                .readDate(notification.getReadDate())
                .build();
    }
}
