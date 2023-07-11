package com.mypill.domain.diary.controller;

import com.mypill.domain.diary.dto.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.diary.service.DiaryService;
import com.mypill.domain.member.entity.Member;

import com.mypill.global.rq.Rq;
import com.mypill.global.rsData.RsData;
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
@RequestMapping("/usr/diary")
@Tag(name = "DiaryController", description = "복약관리")
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
            return rq.historyBack(createRsData);
        }
        return rq.redirectWithMsg("/usr/diary/detail/%s".formatted(createRsData.getData().getId()), createRsData);

    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/list")
    @Operation(summary = "영양제 목록")
    public String showList(Model model) {
        List<Diary> diaries = diaryService.getList(rq.getMember().getId());
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
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/usr/diary/list",deleteRsData);
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @GetMapping("/todolist")
    @Operation(summary = "영양제 기록 체크 폼")
    public String todolist(Model model, String dateStr) {

        Member writer = rq.getMember();
        String today = LocalDate.now().toString();

        List<Diary> diaries = diaryService.getList(writer.getId());

        model.addAttribute("today", today);
        model.addAttribute("diaries", diaries);

        LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr);
        List<DiaryCheckLog> history = diaryService.findHistory(writer);

        Map<LocalDate, List<DiaryCheckLog>> groupedData  = history.stream()
                        .collect(Collectors.groupingBy(DiaryCheckLog::getCheckDate));

        List<LocalDate> sortedDates = groupedData.keySet().stream()
                        .sorted(Comparator.reverseOrder()).toList();

        model.addAttribute("groupedData", groupedData);
        model.addAttribute("sortedDates", sortedDates);
        model.addAttribute("history", history);

        return "usr/diary/todolist";        
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping ("/todolist/toggleCheck/{diaryId}")
    @Operation(summary = "영양제 체크 등록")
    public String toggleCheck(@PathVariable Long diaryId) {
        Member writer = rq.getMember();
        RsData<Diary> diaryRsData = diaryService.toggleCheck(writer, diaryId, LocalDate.now());
        if (diaryRsData.isFail()){
            return rq.historyBack(diaryRsData);
        }
        return rq.redirectWithMsg("/usr/diary/todolist", diaryRsData);
    }
}
