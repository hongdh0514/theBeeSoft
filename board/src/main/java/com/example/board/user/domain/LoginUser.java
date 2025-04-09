package com.example.board.user.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUser {
    private Long userIdx; 
    private String userId; 
    private String userPw; 
    private String userAuth;
    
    // User 엔티티로부터 DTO를 생성하는 정적 팩토리 메소드
    public static LoginUser fromUser(User user) {
    	LoginUser dto = new LoginUser();
        dto.setUserIdx(user.getUserIdx());
        dto.setUserId(user.getUserId());
        dto.setUserPw(user.getUserPw());
        dto.setUserAuth(user.getUserAuth());
        return dto;
    }

    // toString()은 디버깅 시 세션 내용 확인 등에 유용할 수 있습니다. (선택 사항)
    @Override
    public String toString() {
        return "LoginResponseDto{" +
               "userIdx=" + userIdx +
               ", userId='" + userId + '\'' +
               ", userPw='" + userPw + '\'' +
               ", userAuth='" + userAuth + '\'' +
               '}';
    }
}