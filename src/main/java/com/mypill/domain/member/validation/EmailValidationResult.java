package com.mypill.domain.member.validation;

import lombok.Getter;

@Getter
public enum EmailValidationResult {
    VALIDATION_OK("S-1", "사용 가능한 이메일입니다."),
    EMAIL_EMPTY("F-1", "이메일이 입력되지 않았습니다."),
    EMAIL_FORMAT_ERROR("F-2", "올바르지 않은 형식입니다."),
    EMAIL_DUPLICATE("F-3", "이미 사용중인 이메일입니다.");

    private final String resultCode;
    private final String message;

    EmailValidationResult(String resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }
}
