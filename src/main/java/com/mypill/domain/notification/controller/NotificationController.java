package com.mypill.domain.notification.controller;

import com.mypill.domain.notification.dto.response.NotificationResponse;
import com.mypill.domain.notification.entity.Notification;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.base.entitiy.BaseEntity;
import com.mypill.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
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
                .sorted(Comparator.comparing(BaseEntity::getCreateDate).reversed())
                .map(NotificationResponse::of)
                .toList();

        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }

}
