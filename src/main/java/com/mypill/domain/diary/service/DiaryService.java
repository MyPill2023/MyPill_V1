package com.mypill.domain.diary.service;

import com.mypill.domain.diary.dto.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.repository.DiaryRepository;
import com.mypill.domain.member.entity.Member;
import com.mypill.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Transactional
    public List<Diary> getList () {
        return diaryRepository.findByDeleteDateIsNullOrderByCreateDateDesc();
    }
    @Transactional
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




    public Optional<Diary> findById (Long diaryId) {
        return diaryRepository.findById(diaryId);
    }
}