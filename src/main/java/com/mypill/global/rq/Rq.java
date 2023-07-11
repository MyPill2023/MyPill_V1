package com.mypill.global.rq;

import java.util.Date;
import java.util.Map;

import com.mypill.domain.member.entity.Member;
import com.mypill.domain.member.service.MemberService;
import com.mypill.domain.notification.service.NotificationService;
import com.mypill.global.rsdata.RsData;

import com.mypill.global.util.Ut;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
@RequestScope
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final HttpSession session;
    private final User user;
    private final MemberService memberService;
    private Member member = null;
    private final NotificationService notificationService;


    public Rq(HttpServletRequest req, HttpServletResponse resp, HttpSession session, MemberService memberService, NotificationService notificationService) {
        this.req = req;
        this.resp = resp;
        this.session = session;
        this.memberService = memberService;
        this.notificationService = notificationService;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            this.user = null;
        } else if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        } else {
            this.user = null;
        }
    }

    public boolean isLogin() {
        return user != null;
    }

    public boolean isLogout() {
        return !isLogin();
    }

    public Member getMember() {
        if (isLogout()) {
            return null;
        }
        if (member == null) {
            member = memberService.findByUsername(user.getUsername()).orElseThrow();
        }
        return member;
    }

    public boolean isBuyer() {
        if (isLogout()) return false;

        return getMember().isBuyer();
    }

    public boolean isSeller() {
        if (isLogout()) return false;

        return getMember().isSeller();
    }

    public boolean isWaiter() {
        if (isLogout()) return false;

        return getMember().isWaiter();
    }

    public String historyBack(String msg) {
        String referer = req.getHeader("referer");
        String key = "historyBackErrorMsg___" + referer;
        req.setAttribute("localStorageKeyAboutHistoryBackErrorMsg", key);
        req.setAttribute("historyBackErrorMsg", msg);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "common/js";
    }

    public String historyBack(RsData rsData) {
        return historyBack(rsData.getMsg());
    }

    public String redirectWithMsg(String url, RsData rsData) {
        return redirectWithMsg(url, rsData.getMsg());
    }

    public String redirectWithMsg(String url, String msg) {
        return "redirect:" + urlWithMsg(url, msg);
    }

    private String urlWithMsg(String url, String msg) {
        return Ut.url.modifyQueryParam(url, "msg", msgWithTtl(msg));
    }

    private static String msgWithTtl(String msg) {
        return Ut.url.encode(msg) + ";ttl=" + new Date().getTime();
    }

    public String getParamsJsonStr() {
        Map<String, String[]> parameterMap = req.getParameterMap();
        return Ut.json.toStr(parameterMap);
    }

    public String getPassword() {
        return (String) this.session.getAttribute("password");
    }

    public void invalidateSession() {
        this.session.invalidate();
    }

    public static String urlWithErrorMsg(String url, String errorMsg) {
        return Ut.url.modifyQueryParam(url, "errorMsg", msgWithTtl(errorMsg));
    }

    public boolean hasUnreadNotifications() {
        if (isLogout()) return false;
        return notificationService.countUnreadNotificationsByMember(getMember());
    }
}
