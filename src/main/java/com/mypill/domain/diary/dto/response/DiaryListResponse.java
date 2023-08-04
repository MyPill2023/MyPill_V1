package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiaryListResponse {
    private final List<Diary> diaries;

    public static DiaryListResponse of(List<Diary> diaries){
        return new DiaryListResponse(diaries);
    }
}
