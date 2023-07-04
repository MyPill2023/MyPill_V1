package com.mypill.domain.diary.service;

import com.mypill.domain.diary.dto.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.repository.DiaryCheckLogRepository;
import com.mypill.domain.diary.repository.DiaryRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryCheckLogRepository diaryCheckLogRepository;

    public List<Diary> getList () {
        return diaryRepository.findByDeleteDateIsNullOrderByCreateDateDesc();
    }

    public List<Diary> getList(Member member) {
        return diaryRepository.findByMember(member);
    }

    @Transactional
    public RsData<Diary> create(DiaryRequest diaryRequest, Member member) {

        if (member == null) {
            return RsData.of("F-1", "존재하지 않는 회원입니다");
        }
        Diary newDiary = Diary.builder()
                .member(member)
                .name(diaryRequest.getName())
                .time(diaryRequest.getTime())
                .memo(diaryRequest.getMemo())
                .type(diaryRequest.getType())
                .build();
        diaryRepository.save(newDiary);
        return RsData.of("S-1","영양제 등록이 완료되었습니다.", newDiary);
    }

    public RsData<DiaryCheckLog> check (Long id , Member member) {
        if (id == null) {
            return RsData.of("F-2", "유효하지 않은 다이어리 ID입니다.");
        }
        DiaryCheckLog todayDiaryCheckLog = findByMemberAndCAndCreateDate(member.getId()).orElse(null);

        if(todayDiaryCheckLog != null){
            return RsData.of("F-1","오늘의 기록이 이미 등록되어있습니다.");
        }
        Diary diary = findById(id).orElseThrow();

        DiaryCheckLog newDiaryCheckLog = DiaryCheckLog.of(diary,member);

        diaryCheckLogRepository.save(newDiaryCheckLog);
        return RsData.of("S-1","오늘이 복약기록이 등록되었습니다.", newDiaryCheckLog);
    }

    public Optional<Diary> findById (Long diaryId) {
        if (diaryId == null) {
            return Optional.empty();
        }
        return diaryRepository.findById(diaryId);
    }

    public Optional<DiaryCheckLog> findByMemberAndCAndCreateDate(Long memberId) {
        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfDay = currentDate.atStartOfDay();
        LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);
        return diaryCheckLogRepository.findByMemberAndCreateDateBetween(memberId, startOfDay, endOfDay);
    }



}
