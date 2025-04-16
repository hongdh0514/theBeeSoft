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

        System.out.println("[Controller] login request id : " + userId);

        if (userId == null || userId.trim().isEmpty() || userPw == null || userPw.trim().isEmpty()) {
            System.out.println("[Controller] login fail : id or pw empty");
            return "failure";
        }

        Optional<User> loginResult = userService.findUser(userId, userPw);

        if (loginResult.isPresent()) {
            User loginUser = loginResult.get();
//            if (loginCheck(session)) {
//                returnMsg = "failure_login_session";
//            }
            String returnMsg = "";
            if (loginCheck(session)) {
                session.removeAttribute("loginUser");
                returnMsg = "success_session_off";
            }
            else {
                returnMsg = "success";
            }

            session.setAttribute("loginUser", loginUser);
            System.out.println("[Controller] login success id : " + loginUser.getUserId());

            return returnMsg;
        } else {
            System.out.println("[Controller] login fail");
            return "failure";
        }
    }
    
    @PostMapping("/join")
    @ResponseBody
    public String join(@RequestBody User user, HttpSession session) {

        System.out.println("[Controller] join request id : " + user.getUserId());
        
        if (userService.saveUser(user)) {
//            if (loginCheck(session)) {
//                return "failure_login_session";
//            }

            String returnMsg = "";
            if (loginCheck(session)) {
                session.removeAttribute("loginUser");
                returnMsg = "success_session_off";
            }
            else {
                returnMsg = "success";
            }

            session.setAttribute("loginUser", user);
            System.out.println("[Controller] join success id : " + user.getUserId());

            return returnMsg;
        } else {
            System.out.println("[Controller] join fail id (duplication) " + user.getUserId());
            return "failure";
        }
    }

    public boolean loginCheck(HttpSession session){
        if (session.getAttribute("loginUser") != null) {
            System.out.println("[Controller] already login session");
            return true;
        }
        return false;
    }
}