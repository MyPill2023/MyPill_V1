package com.mypill.domain.diary.entity;

import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class diaryEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int time;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String memo;


    public String getTimeDisplayName() {
        return switch (time) {
            case 1 -> "아침";
            case 2 -> "점심";
            case 3 -> "저녁";
            default -> "자기전";
        };
    }
}
