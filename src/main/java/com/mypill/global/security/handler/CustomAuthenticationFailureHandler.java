package com.mypill.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다.";
        }
        else if (exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 일치하지 않습니다.";
        }
        else{
            errorMessage = "알 수 없는 오류가 발생했습니다.";
        }
        errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);//한글 인코딩 깨지는 문제 방지
        setDefaultFailureUrl("/usr/member/loginFail?error=true&exception=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}