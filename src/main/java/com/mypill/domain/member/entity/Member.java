package com.mypill.domain.member.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Column;
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
    @Column(unique = true, length = 15)
    private String userId;
    @NotNull
    @Column(length = 8)
    private String username;
    @NotNull
    @Column(length = 80)
    private String password;
    @NotNull
    private Integer userType;
    @NotNull
    @Column(unique = true, length = 30)
    private String email;

}
