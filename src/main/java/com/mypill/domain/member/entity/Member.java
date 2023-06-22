package com.mypill.domain.member.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

    @NotNull
    private String userId;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private Integer userType;
    @NotNull
    private String email;

}
