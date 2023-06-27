package com.mypill.domain.member.exception;

public class AlreadyJoinException extends RuntimeException {
    public AlreadyJoinException(String msg) {
        super(msg);
    }
}
