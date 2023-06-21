package com.mypill.domain.member.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
public class Member extends BaseEntity {

    private String userId;
    private String username;
    private String password;
    private Integer userType;
    private String email;

}
