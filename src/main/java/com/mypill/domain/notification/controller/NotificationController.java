package com.mypill.domain.notification.controller;

import com.mypill.domain.notification.dto.response.*;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.rq.Rq;
import com.mypill.global.rsdata.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "NotificationController", description = "알림")
public class NotificationController {

    private final NotificationService notificationService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    @Operation(summary = "알림 목록 페이지")
    public String showList(Model model) {
        List<Notification> notifications = notificationService.findByMemberId(rq.getMember().getId());
        model.addAttribute("response", NotificationsResponse.of(notifications));
        return "usr/notification/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/read/{notificationId}")
    @Operation(summary = "알림 읽음 처리")
    public String read(@PathVariable Long notificationId) {
        RsData<Notification> readRsData = notificationService.markAsRead(rq.getMember(), notificationId);
        if (readRsData.isFail()) {
            return rq.historyBack(readRsData);
        }
        return "usr/notification/list";
    }
}
