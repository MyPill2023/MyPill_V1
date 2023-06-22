package com.mypill.domain.question.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@RequiredArgsConstructor
@SuperBuilder
@Builder
public class Question extends BaseEntity {

    private String title;
    private String content;
}
