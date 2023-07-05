package com.mypill.domain.diary.service;

import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.repository.DiaryCheckLogRepository;
import com.mypill.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DiaryCheckLogService {

    private final DiaryCheckLogRepository diaryCheckLogRepository;

    @Transactional
    public DiaryCheckLog save(Member member, String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findByDiaryName(name)
                .map(diaryCheckLog -> {
                    diaryCheckLog.revive();
                    return diaryCheckLog;
                })
                .orElseGet(() -> diaryCheckLogRepository.save(DiaryCheckLog.builder().member(member).name(name).build()));
    }

    private Optional<DiaryCheckLog> findByDiaryName (String name) {
        return diaryCheckLogRepository.findByDiaryName(name);
    }
    public Optional<DiaryCheckLog> findById (Long diaryId) {
        return diaryCheckLogRepository.findByDeleteDateNullAndId(diaryId);
    }


}
