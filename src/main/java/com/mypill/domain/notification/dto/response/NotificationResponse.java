package com.mypill.domain.notification.dto.response;


import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@SuperBuilder(toBuilder = true)
public class NotificationResponse {
    private Long id;
    private LocalDateTime createDate;
    private NotificationTypeCode typeCode;
    private LocalDateTime readDate;
    private Long orderId;
    private String orderNumber;
    private OrderStatus newStatus;
    private String productName;
    private String diaryName;
    private LocalTime diaryTime;

    public static NotificationResponse of(Notification notification) {
        NotificationResponse response =  NotificationResponse.builder()
                .id(notification.getId())
                .typeCode(notification.getTypeCode())
                .createDate(notification.getCreateDate())
                .readDate(notification.getReadDate())
                .build();
        return setResponse(notification, response);
    }

    private static NotificationResponse setResponse(Notification notification, NotificationResponse response) {
        switch (notification.getTypeCode()) {
            case OrderStatus :
                return response.toBuilder()
                        .orderId(notification.getOrderItem().getOrder().getId())
                        .newStatus(notification.getNewStatus())
                        .productName(notification.getOrderItem().getProduct().getName())
                        .build();
            case Record :
                return response.toBuilder()
                        .diaryName(notification.getDiaryName())
                        .diaryTime(notification.getDiaryTime())
                        .build();
            default :
                return response.toBuilder()
                        .orderId(notification.getOrder().getId())
                        .orderNumber(notification.getOrder().getOrderNumber())
                        .build();
        }
    }

}