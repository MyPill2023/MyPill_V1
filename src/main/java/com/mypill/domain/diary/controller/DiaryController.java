package com.mypill.domain.diary.controller;

import com.mypill.domain.diary.dto.request.DiaryRequest;
import com.mypill.domain.diary.dto.response.DiaryCheckListResponse;
import com.mypill.domain.diary.dto.response.DiaryCheckLogResponse;
import com.mypill.domain.diary.dto.response.DiaryListResponse;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.member.entity.Member;

import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/diary")
@Tag(name = "DiaryController", description = "복약관리")
public class DiaryController {

    private final DiaryService diaryService;
    private final Rq rq;

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/create")
    @Operation(summary = "복용 스케줄 등록 페이지")
    public String showCreateForm() {
        return "usr/diary/create";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/create")
    @Operation(summary = "복용 스케줄 등록")
    public String create(@Valid DiaryRequest diaryRequest) {
        RsData<Diary> createRsData = diaryService.create(diaryRequest, rq.getMember());
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }
        return rq.redirectWithMsg("/diary/list", createRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/list")
    @Operation(summary = "복용 스케줄 목록 페이지")
    public String showList(Model model) {
        List<Diary> diaries = diaryService.getList(rq.getMember().getId());
        model.addAttribute("diaryResponse", DiaryListResponse.of(diaries));
        return "usr/diary/list";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/delete/{diaryId}")
    @Operation(summary = "복용 스케줄 정보 삭제")
    public String delete(@PathVariable Long diaryId) {
        RsData<Diary> deleteRsData = diaryService.delete(diaryId, rq.getMember());
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/diary/list", deleteRsData);
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping("/checklist")
    @Operation(summary = "복용 스케줄 기록 체크 페이지")
    public String showCheckLog(Model model) {
        Member writer = rq.getMember();
        String today = LocalDate.now().toString();
        List<Diary> diaries = diaryService.getList(writer.getId());
        List<DiaryCheckLog> history = diaryService.findHistory(writer.getId());
        model.addAttribute("diaryResponse", DiaryCheckListResponse.of(today, diaries, history));
        return "usr/diary/checklist";
    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/check/{diaryId}")
    @Operation(summary = "복용 스케줄 기록 체크 등록")
    public String toggleCheck(@PathVariable Long diaryId) {
        Member writer = rq.getMember();
        RsData<Diary> diaryRsData = diaryService.toggleCheck(writer, diaryId, LocalDate.now());
        if (diaryRsData.isFail()) {
            return rq.historyBack(diaryRsData);
        }
        return rq.redirectWithMsg("/diary/checklist", diaryRsData);
    }
}
