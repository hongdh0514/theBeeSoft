package com.example.board.user.service;

import com.example.board.user.repository.UserRepository;

import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor // Lombok이 자동으로 생성자 추가
public class UserService {
	
	@Autowired
	private final UserRepository userRepository;
	
	public Optional<User> login(String userId, String userPw) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getUserPw().equals(userPw)) {
                return optionalUser;
            }
        }
        return Optional.empty();
    }
	
	public boolean registerUser(User user) {
        // 중복 ID 체크
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            System.out.println("중복된 ID: " + user.getUserId());
            return false;
        }

        // 디버깅 출력 (필드별로 명시적 출력)
        System.out.println("회원가입 시도 - userId: " + user.getUserId() + 
                          ", userPw: " + user.getUserPw() + 
                          ", userAuth: " + user.getUserAuth());
        // 기본 권한 설정
        user.setUserAuth("user");
        // 사용자 저장
        userRepository.save(user);

        System.out.println("회원가입 성공 - userId: " + user.getUserId());
        return true;
    }
}

