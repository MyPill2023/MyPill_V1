package com.mypill.domain.email.entity;

import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class SendEmailLog extends BaseEntity {
    private String resultCode;
    private String message;
    private String email;
    private String subject;
    private String body;
    private LocalDateTime sendEndDate;
    private LocalDateTime failDate;

    public static SendEmailLog of(String email, String subject, String body){
        return SendEmailLog.builder()
                .email(email)
                .subject(subject)
                .body(body)
                .build();
    }
}