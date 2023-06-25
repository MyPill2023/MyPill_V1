package com.mypill.domain.member.form;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JoinForm {
    @NotEmpty
    private String username;
    @NotEmpty
    private String name;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
    @NotEmpty
    private String userType;
}
