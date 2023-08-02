package com.mypill.domain.attr.entity;

import com.mypill.global.base.entity.BaseEntity;
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
                @Index(name = "uniqueIndex1", columnList = "relTypeCode, relId, typeCode, type2Code", unique = true),
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