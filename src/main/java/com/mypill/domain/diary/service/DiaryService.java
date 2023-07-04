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

        DiaryCheckLog todayDiaryCheckLog = findByMemberAndCAndCreateDate(member.getId()).orElse(null);

        if(todayDiaryCheckLog != null){
            return RsData.of("F-1","오늘의 기록이 이미 등록되어있습니다.");
        }
        Diary diary = findById(id).orElseThrow();
        DiaryCheckLog newDiaryCheckLog = DiaryCheckLog.of(diary,member);

        return RsData.of("S-1","오늘이 복약기록이 등록되었습니다.", newDiaryCheckLog);
    }

    public Optional<Diary> findById (Long diaryId) {
        return diaryRepository.findById(diaryId);
    }

    public Optional<DiaryCheckLog> findByMemberAndCAndCreateDate(Long memberId){
        return diaryCheckLogRepository.findByMemberAndCreateDate(memberId, LocalDate.now());
    }


}
