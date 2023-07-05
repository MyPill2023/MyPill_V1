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

    public List<Diary> findAll() {
        return diaryRepository.findByDeleteDateNull();
    }

    @Transactional
    public Diary save(Member member, String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findByName(name)
                .map(diary -> {
                    diary.revive();
                    return diary;
                })
                .orElseGet(() -> diaryRepository.save(Diary.builder().member(member).name(name).build()));

    }

    private Optional<Diary> findByName (String name) {

        return diaryRepository.findByName(name);
    }

    public Optional<Diary> findById (Long diaryId) {
        return diaryRepository.findByDeleteDateNullAndId(diaryId);
    }

    @Transactional
    public void toggleCheck(Member member, Long diaryId, LocalDate checkDate){
        Diary diary = findById(diaryId).orElse(null);

        if (diary == null) return;
        diary.toggleDiaryCheckLog(checkDate);
    }

}
