package com.mypill.global.event;

import com.mypill.domain.diary.entity.Diary;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventBeforeDiaryCheck extends ApplicationEvent {
    private final Diary diary;

    public EventBeforeDiaryCheck(Object source, Diary diary) {
        super(source);
        this.diary = diary;
    }
}
