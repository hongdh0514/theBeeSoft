package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.ui.Model;
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

//    게시글 상세 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<String> checkBoard(@PathVariable Long id, HttpSession session) {
//
//        User loginUser = (User) session.getAttribute("loginUser");
//        String result = boardService.checkBoardApi(id, loginUser);
//
//        return ResponseEntity.ok(result);
//    }

    //게시글 삭제
//    @DeleteMapping("/{id}")
//    public void deleteBoard(@PathVariable Long id) {
//        boardService.deleteById(id);
//    }

    // 게시글 등록
    @PostMapping
    public Board createBoard(@RequestBody @NonNull Board board) {
        return boardService.save(board);
    }
}