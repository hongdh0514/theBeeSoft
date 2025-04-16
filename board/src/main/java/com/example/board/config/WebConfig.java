package com.example.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

//    @Autowired
//    private BoardInterceptor boardInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .order(1) // 인터셉터 실행 순서 지정 (낮을수록 먼저 실행)
                .addPathPatterns("/**") // 모든 경로에 인터셉터 적용
                .excludePathPatterns( // 인터셉터 적용 제외 경로 지정
                        "/",              // 메인 페이지 (로그인 없이 접근 가능해야 한다면)
                        "/favicon.ico",
                        "/css/**",        // 정적 리소스
                        "/js/**",
                        "/images/**",
                        "/error",         // 오류 페이지
                        "/api/user/login",// 로그인 API 요청
                        "/user/logout",    // 로그아웃 요청 (로그인 상태에서만 가능하지만, 여기서 제외해야 무한 리다이렉트 방지)
                        "/user/join",
                        "/user/join/**", // 회원가입 페이지 등 로그인 없이 접근 가능해야 하는 모든 경로 추가
                        "/api/user/join" // 회원가입 API 등
                );// 게시글 작성자 체크 인터셉터
//        registry.addInterceptor(boardInterceptor)
//                .order(2) // 로그인 체크 후 실행
//                .addPathPatterns(
//                        "/board/{id}",      // 게시글 상세 페이지
//                        "/api/board/{id}"   // 게시글 API (조회, 삭제 등)
//                )
//                .excludePathPatterns(
//                        "/board",
//                        "/board/new",
//                        "/board/save"
//                );
    }
}
