package com.mypill.domain.diary.entity;

import com.mypill.domain.member.entity.Member;
import com.mypill.global.base.entitiy.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @OneToMany(mappedBy = "diary",cascade = CascadeType.ALL,orphanRemoval = true)
    private final List<DiaryCheckLog> timeChecks = new ArrayList<>();


    public void removeDiaryCheckLog(DiaryCheckLog diaryCheckLog) {
        timeChecks.remove(diaryCheckLog);
    }

    public DiaryCheckLog addDiaryCheckLog (LocalDate now) {
        return DiaryCheckLog.builder()
                .diary(this)
                .checkDate(now)
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

    public void revive() {
        deleteDate = null;
    }

}
