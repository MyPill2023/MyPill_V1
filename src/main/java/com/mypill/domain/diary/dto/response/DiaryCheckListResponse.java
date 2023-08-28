package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
@Builder
public class DiaryCheckListResponse {
    private String today;
    private List<DiaryResponse> diaries;
    private Map<LocalDate, List<DiaryCheckLog>> groupedData;

    public static DiaryCheckListResponse of(String today, List<Diary> diaries, Map<LocalDate, List<DiaryCheckLog>> groupedData) {
        return DiaryCheckListResponse.builder()
                .today(today)
                .diaries(diaries.stream().map(diary -> DiaryResponse.of(diary, diary.getMember(),today)).toList())
                .groupedData(groupedData)
                .build();
    }
}
