//package com.example.board.config;
//
//import com.example.board.board.domain.Board;
//import com.example.board.board.service.BoardService;
//import com.example.board.user.domain.User;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class BoardInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private BoardService boardService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        String requestURI = request.getRequestURI();
//        System.out.println("[Board Interceptor] request URI: " + requestURI);
//
//        // URI에서 게시글 ID 추출
//        String[] uriParts = requestURI.split("/");
//        Long boardId = Long.parseLong(uriParts[uriParts.length - 1]);
//
//        // 세션에서 로그인 사용자 정보 가져오기
//        HttpSession session = request.getSession(false);
//
//        User loginUser = (User) session.getAttribute("loginUser");
//        String loginUserId = loginUser.getUserId();
//
//        // Admin 체크
//        if (loginUserId.toLowerCase().contains("admin")) {
//            System.out.println("[Board Interceptor] Admin user");
//            return true;
//        }
//
//        // 게시글 정보 가져오기
//        Board board = boardService.findById(boardId).orElse(null);
//
//        // 게시글 유무 체크
//        if (board == null) {
//            session.setAttribute("errorMessage", "존재하지 않는 게시글입니다.");
//            System.out.println("[Board Interceptor] Board not found");
//            response.sendRedirect("/board");
//            return false;
//        }
//
//        // 작성자와 로그인 사용자 비교
//        String boardWriter = board.getWriter();
//            if (!loginUserId.equals(boardWriter)) {
//                session.setAttribute("errorMessage", "해당 작업 권한이 없습니다.");
//            System.out.println("[Board Interceptor] User not authorized");
//            response.sendRedirect("/board");
//            return false;
//        }
//
//        System.out.println("[Board Interceptor] User authorized");
//        return true;
//    }
//}