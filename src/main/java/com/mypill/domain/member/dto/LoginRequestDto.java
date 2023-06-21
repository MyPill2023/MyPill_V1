package com.mypill.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotEmpty(message="아이디를 입력해주세요.")
    private String userId;
    @NotEmpty(message="비밀번호를 입력해주세요.")
    private String password;
}
