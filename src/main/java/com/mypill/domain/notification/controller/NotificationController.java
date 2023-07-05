package com.mypill.domain.notification.controller;

import com.mypill.domain.notification.dto.response.NotificationResponse;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final Rq rq;

    @GetMapping("/list")
    public String list(Model model){

        List<NotificationResponse> notifications = notificationService.findByMemberId(rq.getMember().getId())
                .stream()
                .map(NotificationResponse::of)
                .toList();

        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }


    @PostMapping("/read/{notificationId}")
    public String list(@PathVariable Long notificationId){

        notificationService.makeAsRead(notificationId);

        return "usr/notification/list";
    }
}
