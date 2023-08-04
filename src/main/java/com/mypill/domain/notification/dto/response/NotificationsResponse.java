package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NotificationsResponse {
    private List<Notification> notifications;

    public static NotificationsResponse of(List<Notification> notifications) {
        return new NotificationsResponse(notifications);
    }
}
