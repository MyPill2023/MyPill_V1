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
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryCheckLogRepository diaryCheckLogRepository;


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

    @Transactional
    public RsData<Diary> toggleCheck(Member member, Long diaryId, LocalDate checkDate){

        Diary diary = findById(diaryId).orElse(null);
        if (diary == null) {
            return RsData.of("F-1", "존재하지 않는 영양제입니다.",null);
        }

        if (!Objects.equals(member.getId(), diary.getMember().getId())) {
             return RsData.of("F-2", "본인만 체크 할 수 있습니다");
        }

        diary.toggleDiaryCheckLog(checkDate);
        return RsData.of("S-1","체크 되었습니다.");
    }

    @Transactional
    public RsData<Diary> delete(Long diaryId, Member member) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(IllegalArgumentException::new);
        if(diary == null) {
            return RsData.of("F-1","존재하지 않는 영앙제입니다.", null);
        }
        if(!Objects.equals(diary.getMember().getId(),member.getId())) {
            return RsData.of("F-2","영양제 작성자만 삭제가 가능합니다",diary);
        }
        diary = diary.toBuilder().deleteDate(LocalDateTime.now()).build();
        diaryRepository.save(diary);
        return RsData.of("S-1","영양제가 삭제 되었습니다.",diary);
    }

    public List<Diary> findHistory(Member member, LocalDate date) {

        List<DiaryCheckLog> diaryCheckLogs = diaryCheckLogRepository.findByCheckDate(date);

        return diaryCheckLogs
                .stream()
                .filter(e -> Objects.equals(e.getDiary().getMember().getId(), member.getId()))
                .map(DiaryCheckLog::getDiary)
                .toList();
    }

    public List<Diary> findByMemberId (Long memberId) {
        return diaryRepository.findByMemberId(memberId);
    }

    public List<Diary> getList (Long memberId) {
          return diaryRepository.findByMemberIdAndDeleteDateIsNullOrderByCreateDateDesc(memberId);
    }

    public Optional<Diary> findById (Long diaryId) {
        return diaryRepository.findByDeleteDateNullAndId(diaryId);
    }

}
