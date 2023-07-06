package com.mypill.domain.diary.controller;

import com.mypill.domain.diary.dto.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.service.DiaryCheckLogService;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.member.entity.Member;



import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final DiaryCheckLogService diaryCheckLogService;
    private final Rq rq;


    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/create")
    @Operation(summary = "영양제 등록 폼")
    public String create() {

        return "usr/diary/create";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/create")
    @Operation(summary = "영양제 등록")
    public String create(@Valid DiaryRequest diaryRequest) {

        RsData<Diary> createRsData = diaryService.create(diaryRequest, rq.getMember());
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/diary/detail/%s".formatted(createRsData.getData().getId()), createRsData);

    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/list")
    @Operation(summary = "영양제 목록")
    public String showList(Model model) {
        List<Diary> diaries = diaryService.getList();
        model.addAttribute("diaries", diaries);
        return "usr/diary/list";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/detail/{diaryId}")
    @Operation(summary = "영양제 정보 상세")
    public String showDetail(@PathVariable Long diaryId, Model model) {
        Diary diary = diaryService.findById(diaryId).orElse(null);
        if(diary == null) {
            return rq.historyBack("존재하지 않는 영양제 정보입니다.");
        }
        if (diary.getDeleteDate() != null){
            return rq.historyBack("삭제된 영양제 정보 입니다.");
        }
        model.addAttribute("diary",diary);
        return "usr/diary/detail";
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/list/delete/{diaryId}")
    @Operation(summary = "영양제 정보 삭제")
    public String delete(@PathVariable Long diaryId) {
        RsData<Diary> deleteRsData = diaryService.delete(diaryId, rq.getMember());

        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData.getMsg());
        }
        return rq.redirectWithMsg("/usr/diary/list",deleteRsData);
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/todolist")
    @Operation(summary = "하루 달성 체크 폼")
    public String todolist(Model model, String dateStr) {

        String today = LocalDate.now().toString();

        List<Diary> diaries = diaryService.findAll();
        model.addAttribute("today", today);
        model.addAttribute("diaries", diaries);

        LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr);
        List<Diary> history = diaryService.findHistory(date);

        model.addAttribute("history", history);

        List<DiaryCheckLog> diaryCheckLogList = diaryCheckLogService.findByCheckDate(date);
        model.addAttribute("diaryCheckLog",diaryCheckLogList);
        return "usr/diary/todolist";        
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping ("/todolist/toggleCheck/{diaryId}")
    public String toggleCheck(@PathVariable Long diaryId, Member member) {
        diaryService.toggleCheck(member, diaryId, LocalDate.now());

        return "redirect:/usr/diary/todolist";
    }

}
