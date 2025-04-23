package com.example.board.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 실행 전에 호출됩니다.
     * 세션에 로그인 정보가 없으면 로그인 페이지로 리다이렉트시킵니다.
     * @return true: 컨트롤러 실행 계속 / false: 컨트롤러 실행 중단
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        // 요청 URI 확인 (디버깅용)
        String requestURI = request.getRequestURI();
        System.out.println("[Login Interceptor] request URI: " + requestURI);

        // 세션 가져오기 (없으면 null 반환)
        HttpSession session = request.getSession(false);

        // 세션이 없거나, 세션에 "loginUser" 속성이 없으면

//        "" 값으로 없는값 체크
        if (session == null || session.getAttribute("loginUser") == null) {
            System.out.println("[Login Interceptor] no session user");

            // 로그인 페이지로 리다이렉트
            // 로그인 후 원래 가려던 페이지로 이동시키기 위해 redirectURL 파라미터 추가 (선택 사항)
            String redirectURL = request.getContextPath() + "/?redirectURL=" + requestURI;
            response.sendRedirect(redirectURL);
            return false; // 컨트롤러 실행하지 않음
        }

        // 세션이 유효하면 (로그인 상태)
        System.out.println("[Login Interceptor] session user");
        return true; // 컨트롤러 실행 계속
    }
}
