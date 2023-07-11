package com.mypill.global.security.handler;

import com.mypill.global.rq.Rq;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final Rq rq;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (!rq.getMember().isEmailVerified()) {
            redirectStrategy.sendRedirect(request, response, Rq.urlWithErrorMsg("/member/login", "이메일 인증이 완료되지 않은 계정입니다."));
            clearAuthenticationAttributes(request);
            rq.invalidateSession();
            return;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}