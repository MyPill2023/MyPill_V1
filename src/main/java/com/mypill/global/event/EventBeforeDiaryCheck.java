package com.mypill.global.event;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalTime;

@Getter
public class EventBeforeDiaryCheck extends ApplicationEvent {
    private final Diary diary;

    public EventBeforeDiaryCheck(Object source,Diary diary){
        super(source);
        this.diary = diary;
    }

}
