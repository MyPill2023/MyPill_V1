package com.mypill.domain.diary.controller;


import com.mypill.domain.diary.dto.DiaryCheckLogRequest;
import com.mypill.domain.diary.dto.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/diary")
public class DiaryController {

    private final DiaryService diaryService;
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
    
    @GetMapping("/todolist")
    public String todolist(Model model) {
        List<Diary> diaries = diaryService.getList();
        model.addAttribute("diaries", diaries);
        return "usr/diary/todolist";        
    }

    @PostMapping("/todolist")
    @Operation(summary = "하루 달성 체크")
    public String checked(DiaryCheckLogRequest diaryCheckLogRequest, Member member) {
        RsData<DiaryCheckLog> checkRsData = diaryService.check(diaryCheckLogRequest.getId(), member);

        if(checkRsData.isFail()){
            return rq.historyBack(checkRsData);
        }

        return rq.redirectWithMsg("/usr/diary/todolist", checkRsData);
    }

}
