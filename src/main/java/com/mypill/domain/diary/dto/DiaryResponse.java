package com.mypill.domain.diary.dto;

import com.mypill.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiaryResponse {

    private Member member;
    private String name;
    private String time;
    private String type;
    private String memo;

}
