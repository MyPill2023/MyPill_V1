package com.mypill.domain.diary.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
public class Diary extends BaseEntity {

    @ManyToOne
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String time;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String memo;

    public String getTimeDisplayName() {
        return switch (time) {
            case "morning" -> "아침";
            case "lunch" -> "점심";
            case "dinner" -> "저녁";
            default -> "자기전";
        };
    }
}
