package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DiaryListResponse {
    private List<DiaryResponse> diaries;

    public static DiaryListResponse of(List<Diary> diaries) {
        return DiaryListResponse.builder()
                .diaries(diaries.stream().map(diary -> DiaryResponse.of(diary, diary.getMember())).toList())
                .build();
    }
}
