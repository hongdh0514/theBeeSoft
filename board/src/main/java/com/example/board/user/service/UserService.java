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
    // null 체크
    if (userId == null || userPw == null) {
        return Optional.empty();
    }

    try {
        // userRepository 에서 userId로 사용자 조회
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        // Optional 이 비어있지 않은지 확인
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 사용자 비밀번호가 null 인지 확인
            if (user.getUserPw() != null && user.getUserPw().equals(userPw)) {
                return optionalUser;
            }
        }
        return Optional.empty();
    } catch (Exception e) {
        // 예외 발생 시 로깅 또는 적절한 처리
        // 예: logger.error("Error while finding user with userId: {}", userId, e);
        return Optional.empty();
    }
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

