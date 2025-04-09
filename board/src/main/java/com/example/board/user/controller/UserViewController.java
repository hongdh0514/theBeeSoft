package com.example.board.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserViewController {
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
       
        return "redirect:/";
    }
    
    @GetMapping("/join")
    public String join() {
    	
    	return "user/join";
    }
}