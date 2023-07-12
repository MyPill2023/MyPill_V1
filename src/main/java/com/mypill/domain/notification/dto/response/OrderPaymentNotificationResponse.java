package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.Notification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderPaymentNotificationResponse extends NotificationResponse {
    private Long orderId;
    private String orderNumber;

    public static OrderPaymentNotificationResponse of(Notification notification) {
        return OrderPaymentNotificationResponse.builder()
                .id(notification.getId())
                .typeCode(notification.getTypeCode())
                .createDate(notification.getCreateDate())
                .orderId(notification.getOrder().getId())
                .orderNumber(notification.getOrder().getOrderNumber())
                .readDate(notification.getReadDate())
                .build();
    }
}
