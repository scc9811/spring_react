package com.spring_react.practice.config;


import com.spring_react.practice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // csrf, cors는 사용 X
                .csrf(CsrfConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(
                        httpRequests -> httpRequests
                                // /api~~ 은 모든 접근을 허용.
                                .requestMatchers("/api/user/login").permitAll()
                                // 나머지 리소스(anyRequest) 접근에 대해서는 인증(authenticated)을 거쳐야 함.
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
