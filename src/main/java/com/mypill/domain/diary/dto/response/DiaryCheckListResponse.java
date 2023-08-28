package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Data
@Builder
public class DiaryCheckListResponse {
    private String today;
    private List<DiaryResponse> diaries;
    private Map<LocalDate, List<DiaryCheckLogResponse>> groupedData;

    public static DiaryCheckListResponse of(String today, List<Diary> diaries, List<DiaryCheckLog> history) {
        return DiaryCheckListResponse.builder()
                .today(today)
                .diaries(diaries.stream().map(diary -> DiaryResponse.of(diary, diary.getMember(),today)).toList())
                .groupedData(history.stream()
                        .sorted(Comparator.comparing(DiaryCheckLog::getCreateDate))
                        .map(DiaryCheckLogResponse::of)
                        .collect(Collectors.groupingBy(DiaryCheckLogResponse::getCheckDate)))
                .build();
    }
}
