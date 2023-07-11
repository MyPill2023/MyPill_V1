package com.mypill.domain.diary.dto;

import com.mypill.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class DiaryResponse {

    private Member member;
    private String name;
    private LocalTime time;
    private String type;
    private String memo;

}
