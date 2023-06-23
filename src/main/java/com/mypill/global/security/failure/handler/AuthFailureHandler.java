package com.mypill.global.security.failure.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        request.getSession().setAttribute("userId", userId);
        request.getSession().setAttribute("password", password);
        response.sendRedirect("/usr/member/login/auth/fail");
    }
}
