package com.mypill.domain.diary.dto.response;


import com.mypill.domain.diary.entity.DiaryCheckLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DiaryCheckLogResponse {

    private String diaryName;
    private LocalDateTime createDate;
    private LocalDate checkDate;

    public static DiaryCheckLogResponse of(DiaryCheckLog diaryCheckLog) {
        return DiaryCheckLogResponse.builder()
                .diaryName(diaryCheckLog.getDiary().getName())
                .createDate(diaryCheckLog.getCreateDate())
                .checkDate(diaryCheckLog.getCheckDate())
                .build();
    }
}
