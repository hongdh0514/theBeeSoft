package com.example.board.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 브라우저 캐싱을 방지하기 위한 HTTP 응답 헤더를 설정하는 필터입니다.
 * 로그인이 필요한 페이지 등에 적용하여 뒤로가기 시 캐시된 페이지가 보이는 것을 방지합니다.
 */
@Component // Spring이 이 필터를 자동으로 인식하고 적용하도록 합니다.
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // ServletResponse를 HttpServletResponse로 캐스팅합니다.
        HttpServletResponse response = (HttpServletResponse) res;

        // 캐시 제어 관련 헤더 설정
        // HTTP 1.1
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        // HTTP 1.0
        response.setHeader("Pragma", "no-cache");
        // Proxies
        response.setDateHeader("Expires", 0); // 또는 과거 날짜 설정

        // 다음 필터 또는 서블릿으로 요청/응답 전달
        chain.doFilter(req, res);
    }

    // Filter 인터페이스의 다른 메서드 (init, destroy)는 기본 구현을 사용하거나 비워둡니다.
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필터 초기화 로직 (필요 시)
    }

    @Override
    public void destroy() {
        // 필터 소멸 로직 (필요 시)
    }
}
