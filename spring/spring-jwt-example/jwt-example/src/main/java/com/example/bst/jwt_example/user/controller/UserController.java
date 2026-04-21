package com.example.bst.jwt_example.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bst.jwt_example.user.entity.User;
import com.example.bst.jwt_example.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("/join")
    public String postMethodName(@RequestBody User user) {
        userService.join(user);
        return "회원가입 완료";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("manager")
    public String manager() {
        return new String();
    }

    @GetMapping("/admin")
    public String admin() {
        return new String();
    }

}
