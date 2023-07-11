package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.NotificationTypeCode;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class NotificationResponse {
    private Long id;
    private LocalDateTime createDate;
    private NotificationTypeCode typeCode;
    private LocalDateTime readDate;
}
