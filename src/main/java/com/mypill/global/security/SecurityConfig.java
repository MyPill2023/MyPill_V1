package com.mypill.global.security;

import com.mypill.global.security.handler.CustomOAuth2SuccessHandler;
import com.mypill.global.security.oauth2.CustomOAuth2AccessTokenResponseClient;
import com.mypill.global.security.handler.CustomAuthenticationFailureHandler;
import com.mypill.global.security.handler.CustomAuthenticationSuccessHandler;
import com.mypill.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuth2AccessTokenResponseClient oAuth2AccessTokenResponseClient;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider bean = new DaoAuthenticationProvider();
        bean.setUserDetailsService(customUserDetailsService);
        bean.setPasswordEncoder(passwordEncoder());
        return bean;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(null)
                )
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/member/login") // GET
                                .loginProcessingUrl("/member/login") // POST
                                .successHandler(customAuthenticationSuccessHandler)
                                .defaultSuccessUrl("/home")
                                .failureHandler(customAuthenticationFailureHandler)
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/member/login")
                                .tokenEndpoint(t -> t
                                        .accessTokenResponseClient(oAuth2AccessTokenResponseClient)
                                )
                                .successHandler(customOAuth2SuccessHandler)
                                .defaultSuccessUrl("/home")
                                .failureHandler(customAuthenticationFailureHandler)
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/member/logout")
                                .invalidateHttpSession(true) // 로그아웃 이후 세션 전체 삭제 여부
                                .deleteCookies("JSESSIONID")
                );
        return http.build();
    }
}