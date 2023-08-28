package com.mypill.domain.diary.dto.response;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class DiaryResponse {
    private Long memberId;
    private String name;
    private LocalTime time;
    private Long diaryId;
    private boolean isCheckedWhen;

    public static DiaryResponse of(Diary diary, Member member, String checkDate) {
        return DiaryResponse.builder()
                .memberId(member.getId())
                .diaryId(diary.getId())
                .name(diary.getName())
                .time(diary.getTime())
                .isCheckedWhen(diary.getTimeChecks().stream()
                        .anyMatch(e -> e.getCheckDate().toString().equals(checkDate)))
                .build();
    }

    public static DiaryResponse of(Diary diary, Member member) {
        return DiaryResponse.builder()
                .memberId(member.getId())
                .diaryId(diary.getId())
                .name(diary.getName())
                .time(diary.getTime())
                .build();
    }
}
