package com.mypill.global.security;

import com.mypill.global.security.filter.JwtAuthorizationFilter;
import com.mypill.global.security.handler.ApiAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
                )
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/*/usr/member/login").permitAll()
                                .anyRequest()
                                .authenticated() // 최소자격 : 로그인
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(STATELESS)
                )
                .formLogin(
                        AbstractHttpConfigurer::disable
                )
                .addFilterBefore(
                        jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .logout(
                        AbstractHttpConfigurer::disable
                );

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/api/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }
}