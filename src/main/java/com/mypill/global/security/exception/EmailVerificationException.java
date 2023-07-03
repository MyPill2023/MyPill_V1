package com.mypill.global.security.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailVerificationException extends AuthenticationException {
    public EmailVerificationException(String msg) {
        super(msg);
    }
    public EmailVerificationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
