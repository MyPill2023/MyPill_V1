package com.mypill.global.security;

import com.mypill.global.security.OAuth2.CustomOAuth2AccessTokenResponseClient;
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
        bean.setHideUserNotFoundExceptions(false);
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
                                .loginPage("/usr/member/login") // GET
                                .loginProcessingUrl("/usr/member/login") // POST
                                .successHandler(customAuthenticationSuccessHandler)
                                .failureHandler(customAuthenticationFailureHandler)
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/usr/member/login")
                                .tokenEndpoint(t -> t
                                        .accessTokenResponseClient(oAuth2AccessTokenResponseClient)
                                )
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/usr/member/logout")
                );

        return http.build();
    }
}