package com.mypill.domain.diary.service;

import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.repository.DiaryCheckLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DiaryCheckLogService {

    private final DiaryCheckLogRepository diaryCheckLogRepository;

    public Optional<DiaryCheckLog> findById (Long diaryId) {
        return diaryCheckLogRepository.findByDeleteDateNullAndId(diaryId);
    }

    public List<DiaryCheckLog> findByCheckDate(LocalDate date) {
        return diaryCheckLogRepository.findByCheckDate(date);
    }
}

