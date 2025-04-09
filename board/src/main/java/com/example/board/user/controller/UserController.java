package com.example.board.user.controller;

import com.example.board.user.domain.User;
import com.example.board.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody User user, HttpSession session) {
        String userId = user.getUserId();
        String userPw = user.getUserPw();

        System.out.println("login request id : " + userId);

        if (userId == null || userId.trim().isEmpty() || userPw == null || userPw.trim().isEmpty()) {
            System.out.println("login fail : id pr pw empty");
            return "failure";
        }

        Optional<User> loginResult = userService.login(userId, userPw);

        if (loginResult.isPresent()) {
            User loginUser = loginResult.get();
            session.setAttribute("loginUser", loginUser);
            session.setMaxInactiveInterval(1800);
            System.out.println("login success id : " + loginUser.getUserId() + ", Session ID: " + session.getId());
            return "success";
        } else {
            System.out.println("login fail id : " + userId);
            return "failure";
        }
    }
    
    @PostMapping("/join")
    @ResponseBody
    public String register(@RequestBody User user, HttpSession session) {
    	
        System.out.println("join request id : " + user.getUserId());
        
        if (userService.registerUser(user)) {
            session.setAttribute("loginUser", user);
            System.out.println("join success id : " + user.getUserId());
            return "success";
        } else {
            System.out.println("join success id (duplication) " + user.getUserId());
            return "failure";
        }
    }
}