package com.example.board.user.service;

import com.example.board.user.repository.UserRepository;

import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

//                null 체크
//                전체를 try catch 문 사용하기 -> 쿼리마다 try catch
	public Optional<User> findUser(String userId, String userPw) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getUserPw().equals(userPw)) {
                return optionalUser;
            }
        }
        return Optional.empty();
    }
	
	public boolean saveUser(User user) {
        // 중복 ID 체크
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            System.out.println("[Service] join fail id (duplication) " + user.getUserId());
            return false;
        }

        System.out.println("[Service] join request - userId: " + user.getUserId() +
                          ", userPw: " + user.getUserPw());
        // 사용자 저장
        userRepository.save(user);

        System.out.println("[Service] join success - userId: " + user.getUserId());
        return true;
    }
}

