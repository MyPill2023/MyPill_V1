package com.mypill.domain.member.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Size(max = 8)
    @Pattern(regexp = "^[ㄱ-힣]{1,8}$")
    private String name;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$", message = "영문, 숫자가 포함되며 8자 이상 12자 이내여야합니다.")
    private String password;
    @NotBlank
    @Email
    @Size(max = 30)
    private String email;
    @NotBlank
    private String userType;
}
