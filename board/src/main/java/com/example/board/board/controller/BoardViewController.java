package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
import com.example.board.user.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;

    // 게시글 목록 조회
    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("boards", boardService.findAll());
        // 세션에서 에러 메시지 전달
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage"); // 세션 삭제
        }
        return "board/list";
    }

    // 게시글 상세 조회
//    @GetMapping("/{id}")
//    public String detail(@PathVariable Long id, HttpSession session, Model model) {
//
//        User loginUser = (User) session.getAttribute("loginUser");
//        return boardService.checkBoard(id, loginUser, model, session);
//    }

    //게시글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/form";
    }

    // 게시글 저장
    @PostMapping("/save")
    public String save(@ModelAttribute Board board) {
        boardService.save(board);
        return "redirect:/board";
    }


    // 게시글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        boardService.deleteById(id);
        session.setAttribute("errorMessage", "삭제 성공");
        return "redirect:/board";
    }

//    게시글 상세 조회
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Optional<Board> boardOptional = boardService.findById(id);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            model.addAttribute("board", board);
            return "board/detail";
        } else {
            return "redirect:/board";
        }
    }
}