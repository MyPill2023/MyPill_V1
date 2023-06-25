package com.mypill.global.security;

import com.mypill.global.security.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

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
                                .loginPage("/usr/member/login") // GET
                                .loginProcessingUrl("/usr/member/login") // POST
                                .successHandler(customAuthenticationSuccessHandler)
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/usr/member/logout")
                );

        return http.build();
    }
}