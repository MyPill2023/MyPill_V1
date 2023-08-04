package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DiaryCheckListResponse {
    private final String today;
    private final List<Diary> diaries;
    private final Map<LocalDate, List<DiaryCheckLog>> groupedData;

    public static DiaryCheckListResponse of(String today, List<Diary> diaries, Map<LocalDate, List<DiaryCheckLog>> groupedData) {
        return new DiaryCheckListResponse(today, diaries, groupedData);
    }
}
