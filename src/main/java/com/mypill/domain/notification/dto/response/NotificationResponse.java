package com.mypill.domain.notification.dto.response;


import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.entity.NotificationTypeCode;
import com.mypill.domain.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
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
        return response.setResponse(notification);
    }

    public NotificationResponse setResponse(Notification notification) {
        switch (notification.getTypeCode()) {
            case OrderStatus :
                this.orderId = notification.getOrderItem().getOrder().getId();
                this.newStatus = notification.getNewStatus();
                this.productName = notification.getOrderItem().getProduct().getName();
                break;
            case Record :
                this.diaryName = notification.getDiaryName();
                this.diaryTime = notification.getDiaryTime();
                break;
            default :
                this.orderId = notification.getOrder().getId();
                this.orderNumber = notification.getOrder().getOrderNumber();
                break;
        }
        return this;
    }

}