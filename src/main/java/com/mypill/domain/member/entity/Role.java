package com.mypill.domain.member.entity;

import lombok.Getter;

@Getter
public enum Role {
    MEMBER("회원", "MEMBER"),
    BUYER("구매자", "BUYER"),
    SELLER("판매자", "SELLER"),
    WAITER("대기자", "WAITER");

    private final String value;
    private final String authority;

    Role(String value, String authority) {
        this.value = value;
        this.authority = authority;
    }

    public static Role findByValue(String value) {
        for (Role role : Role.values())
            if (role.value.equals(value)) {
                return role;
            }
        return null;
    }

}
