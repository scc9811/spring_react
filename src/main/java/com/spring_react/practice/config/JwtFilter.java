package com.spring_react.practice.config;

import com.spring_react.practice.service.UserService;
import com.spring_react.practice.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);


        // token이 없는 경우 block
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("authorization is null");
            filterChain.doFilter(request, response);
            return;
        }

        // Token 꺼내기
        String token = authorization.split(" ")[1];

        // Token expired 여부 검증
        if(JwtUtil.isExpired(token, secretKey)){
            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // UserName Token 꺼내기
        String userName = JwtUtil.getUserName(token, secretKey);
        log.info("userName: {}", userName);

        // 권한부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, List.of(new SimpleGrantedAuthority("USER")));

        // Detail 추가
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
