package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 전체 게시글 조회
    @GetMapping
    public List<Board> getAllBoards(HttpSession session) {
        return boardService.findAll();
    }

    // 게시글 조회
    @GetMapping("/{id}")
    public String getBoardById(
            @PathVariable @NonNull Long id,
            @SessionAttribute(name = "loginUser") User loginUser,
            HttpSession session) {

        if (loginUser == null) {
            return "failure_no_login_user";
        }

        Optional<Board> board = boardService.findById(id);
        if (board.isEmpty()) {
            return "failure_not_found";
        }

        String loginUserId = loginUser.getUserId();
        if (loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            return "success";
        } else {
            return "failure_admin_or_writer";
        }
    }

    // 게시글 등록
    @PostMapping
    public Board createBoard(@RequestBody @NonNull Board board) {
        return boardService.save(board);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public String deleteBoard(
            @PathVariable @NonNull Long id,
            @SessionAttribute(name = "loginUser") User loginUser,
            HttpSession session) {

        if (loginUser == null) {
            return "failure_no_login_user";
        }

        Optional<Board> board = boardService.findById(id);
        if (board.isEmpty()) {
            return "failure_not_found";
        }

        String loginUserId = loginUser.getUserId();
        if (loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            boardService.deleteById(id);
            return "success";
        } else {
            return "failure_admin_or_writer";
        }
    }
}