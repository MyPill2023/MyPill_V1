package com.mypill.domain.diary.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DiaryRequest {

    @NotEmpty(message="영양제 이름을 입력해주세요.")
    private String name;
    @NotEmpty(message="섭취 시간을 선택해주세요.")
    private String time;

    private String type;
    private String memo;

}
