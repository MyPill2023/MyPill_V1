package com.mypill.domain.notification.controller;

import com.mypill.domain.notification.dto.response.NotificationResponse;
import com.mypill.domain.notification.dto.response.OrderPaymentNotificationResponse;
import com.mypill.domain.notification.dto.response.OrderStatusUpdateNotificationResponse;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
@Tag(name = "NotificationController", description = "알림")
public class NotificationController {

    private final NotificationService notificationService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    @Operation(summary = "알림 목록")
    public String list(Model model){

        List<NotificationResponse> notificationsResponse = new ArrayList<>();
        List<Notification> notifications = notificationService.findByMemberId(rq.getMember().getId());
        for (Notification notification : notifications) {
            switch (notification.getTypeCode()) {
                case OrderStatus -> notificationsResponse.add(OrderStatusUpdateNotificationResponse.of(notification));
                default -> notificationsResponse.add(OrderPaymentNotificationResponse.of(notification));
            }
        }
        model.addAttribute("notifications", notificationsResponse);

        return "usr/notification/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/read/{notificationId}")
    @Operation(summary = "알림 읽음 처리")
    public String list(@PathVariable Long notificationId){
        RsData<Notification> readRsData = notificationService.makeAsRead(rq.getMember(), notificationId);
        if(readRsData.isFail()){
            return rq.historyBack(readRsData);
        }
        return "usr/notification/list";
    }
}
