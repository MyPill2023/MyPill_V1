package com.mypill.domain.diary.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @NotNull
    private String name;

    @NotNull
    private LocalTime time;

    @Builder.Default
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DiaryCheckLog> timeChecks = new ArrayList<>();

    public static Diary of(Member member, String name, LocalTime time){
        return Diary.builder()
                .member(member)
                .name(name)
                .time(time)
                .build();
    }

    public void removeDiaryCheckLog(DiaryCheckLog diaryCheckLog) {
        timeChecks.remove(diaryCheckLog);
    }

    public DiaryCheckLog addDiaryCheckLog(LocalDate now) {
        return DiaryCheckLog.builder()
                .diary(this)
                .checkDate(now)
                .member(member)
                .build();
    }

    public void toggleDiaryCheckLog(LocalDate checkDate) {
        DiaryCheckLog diaryCheckLog = timeChecks.stream()
                .filter(e -> e.getCheckDate().equals(checkDate))
                .findFirst()
                .orElse(null);
        if (diaryCheckLog == null) {
            diaryCheckLog = addDiaryCheckLog(checkDate);
            timeChecks.add(diaryCheckLog);
        } else {
            removeDiaryCheckLog(diaryCheckLog);
        }
    }

    public boolean isCheckedWhen(String checkDate) {
        return timeChecks.stream()
                .anyMatch(e -> e.getCheckDate().toString().equals(checkDate));
    }
}
