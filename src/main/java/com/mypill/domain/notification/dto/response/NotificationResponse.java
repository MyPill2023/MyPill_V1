package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {

    private Long id;
    private LocalDateTime createDate;
    private NotificationTypeCode typeCode;
    private Long orderId;
    private OrderStatus newStatus;
    private String productName;
    private LocalDateTime readDate;

    public static NotificationResponse of(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .createDate(notification.getCreateDate())
                .orderId(notification.getOrderItem().getOrder().getId())
                .newStatus(notification.getNewStatus())
                .productName(notification.getOrderItem().getProduct().getName())
                .readDate(notification.getReadDate())
                .build();
    }


}
