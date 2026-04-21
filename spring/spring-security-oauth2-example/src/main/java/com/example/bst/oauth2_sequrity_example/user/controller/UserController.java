package com.example.bst.oauth2_sequrity_example.user.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.bst.oauth2_sequrity_example.user.entity.User;
import com.example.bst.oauth2_sequrity_example.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public @ResponseBody String index() {
        return "인덱스 페이지 입니다.";
    }

    @GetMapping("/user")
    public @ResponseBody String user() {
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/join")
    public String join(User _user) {
        userService.join(_user);
        return "redirect:/login";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "join";
    }

    @GetMapping("/joinProc")
    public String joinProc() {
        return "회원가입 완료됨!";
    }

    @Secured("ROLE_ADMIN") // SecurityConfig 에 method = true 하면 controller에서 할 수 있음
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 이 두개의 권한이 있어야 들어가짐
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "개인정보";
    }
}
