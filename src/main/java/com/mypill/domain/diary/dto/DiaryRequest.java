package com.mypill.domain.diary.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DiaryRequest {
    @NotEmpty(message="영양제 이름을 입력해주세요.")
    private String name;
    private LocalTime time;

}
