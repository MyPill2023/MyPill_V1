package com.mypill.domain.attr.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(
        indexes = {
                // 변수명이 같은 데이터 생성되는것을 막는 역할
                // 특정 변수명으로 검색했을 때 초고속 검색이 되도록
                @Index(name = "uniqueIndex1", columnList = "relTypeCode, relId, typeCode, type2Code", unique = true),
                // 특정 그룹의 데이터들을 불러올 때
                @Index(name = "index1", columnList = "relTypeCode, typeCode, type2Code")
        }
)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Attr extends BaseEntity {
    private String relTypeCode;
    private long relId;
    private String typeCode;
    private String type2Code;
    private String val;
    private LocalDateTime expireDate;
}