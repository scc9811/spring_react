package com.spring_react.practice.controller;

import com.spring_react.practice.dto.LoginRequest;
import com.spring_react.practice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // @RequestBody : Http의 Body부분을 자바 객체로 변환.
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest dto) {
        return ResponseEntity.ok().body(userService.login(dto.getUserName(), ""));
    }
}
