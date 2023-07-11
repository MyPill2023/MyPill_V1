package com.mypill.domain.diary.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;


@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryCheckLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;

    private String name;

    private LocalDate checkDate;
  
    public static DiaryCheckLog of(Diary diary, Member member) {
        return DiaryCheckLog.builder()
                .diary(diary)
                .member(member)
                .checkDate(LocalDate.now())
                .build();
    }

    @Override
    public String toString() {
        return "DiaryCheckLog{" +
                "member=" + member +
                ", diary=" + diary +
                ", name='" + name + '\'' +
                ", checkDate=" + checkDate +
                '}';
    }
}
