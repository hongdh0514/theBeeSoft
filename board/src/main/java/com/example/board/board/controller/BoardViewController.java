package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
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

    //게시글 목록 조회
    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("boards", boardService.findAll());
        return "board/list";
    }

    //게시글 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {

        Optional<Board> board = boardService.findById(id);
        if(board.isEmpty()) {
            return "common/error_404";
        }
        model.addAttribute("board", board.orElse(new Board()));
        return "board/detail";
    }

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

//    // 게시글 삭제
//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable Long id) {
//        boardService.deleteById(id);
//        return "redirect:/board";
//    }
}