package com.mypill.domain.member.validation;

import lombok.Getter;

@Getter
public enum UsernameValidationResult {
    VALIDATION_OK("S-1", "사용 가능한 아이디입니다."),
    USERNAME_EMPTY("F-1", "아이디가 입력되지 않았습니다."),
    USERNAME_DUPLICATE("F-2", "이미 사용중인 아이디입니다.");

    private final String resultCode;
    private final String message;

    UsernameValidationResult(String resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }
}
