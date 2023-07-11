package com.mypill.domain.notification.dto.response;

import com.mypill.domain.notification.entity.Notification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DiaryCheckResponse extends NotificationResponse{

    private String diaryName;
    private LocalTime diaryTime;

    public static DiaryCheckResponse of(Notification notification){
        return DiaryCheckResponse.builder()
                .id(notification.getId())
                .typeCode(notification.getTypeCode())
                .createDate(notification.getCreateDate())
                .diaryName(notification.getDiaryName())
                .diaryTime(notification.getDiaryTime())
                .readDate(notification.getReadDate())
                .build();
    }
}
