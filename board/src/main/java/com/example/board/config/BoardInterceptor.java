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
//    public boolean preHandle(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//        System.out.println("[Board Interceptor] request URI: " + requestURI);
//
//        // URI에서 게시글 ID 추출
//        String[] uriParts = requestURI.split("/");
//        Long boardId = null;
//        try {
//            boardId = Long.parseLong(uriParts[uriParts.length - 1]);
//        } catch (NumberFormatException e) {
//            System.out.println("[Board Interceptor] Invalid board ID");
//            sendErrorResponse(request, response, HttpStatus.BAD_REQUEST, "유효하지 않은 게시글 ID입니다.");
//            return false;
//        }
//
//        // 세션에서 로그인 사용자 정보 가져오기
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("loginUser") == null) {
//            System.out.println("[Board Interceptor] No session or user");
//            sendErrorResponse(request, response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
//            return false;
//        }
//
//        User loginUser = (User) session.getAttribute("loginUser");
//        String loginUserId = loginUser.getUserId();
//
//        // Admin 체크
//        if (loginUserId.toLowerCase().contains("admin")) {
//            System.out.println("[Board Interceptor] Admin user, bypassing writer check");
//            return true;
//        }
//
//        // 게시글 정보 조회
//        Board board = boardService.findById(boardId).orElse(null);
//        if (board == null) {
//            System.out.println("[Board Interceptor] Board not found");
//            sendErrorResponse(request, response, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
//            return false;
//        }
//
//        // 작성자와 로그인 사용자 비교
//        String boardWriter = board.getWriter();
//        if (!loginUserId.equals(boardWriter)) {
//            System.out.println("[Board Interceptor] User not authorized");
//            sendErrorResponse(request, response, HttpStatus.FORBIDDEN, "작성자 또는 관리자만 접근 가능합니다.");
//            return false;
//        }
//
//        System.out.println("[Board Interceptor] User authorized");
//        return true;
//    }
//
//    // JSON 에러 응답 전송
//    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String message) throws Exception {
//        HttpSession session = request.getSession(true);
//        session.setAttribute("errorMessage", message);
//
//        String acceptHeader = request.getHeader("Accept");
//        if (acceptHeader != null && acceptHeader.contains("application/json")) {
//            response.setStatus(status.value());
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            String jsonResponse = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
//            response.getWriter().write(jsonResponse);
//        } else {
//            response.sendRedirect(request.getContextPath() + "/board");
//        }
//    }
//}