package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

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
    @GetMapping("/{id}")
    public String checkBoard(@PathVariable Long id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        return boardService.checkBoard(id, loginUser);
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    public String deleteBoard(@PathVariable Long id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        String result = boardService.checkBoard(id, loginUser);

        if(result.equals("success")) {
            boardService.deleteById(id);
        }
        return result;

    }

    // 게시글 등록
    @PostMapping
    public Board createBoard(@RequestBody @NonNull Board board) {
        return boardService.save(board);
    }
}