package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        model.addAttribute("categories", boardService.findAllCategories());

        // 세션에서 에러 메시지 전달
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage"); // 세션 삭제
        }
        return "board/list";
    }

    //게시글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/form";
    }

    //게시글 작성 폼
    @GetMapping("/new_t")
    public String createForm_t(Model model) {
        model.addAttribute("board", new Board());
        model.addAttribute("categories", boardService.findAllCategories());
        return "/board/boardWrite :: boardWriteBox";
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

    @GetMapping("/{id}/fragment")
    public String getBoardDetail(@PathVariable Long id, Model model) {

        Optional<Board> boardOptional = boardService.findById(id);
        boardOptional.ifPresent(boardDetail -> model.addAttribute("boardDetail", boardDetail));

        return "/board/boardDetail :: boardDetailBox";
    }

//    게시글 검색
    @PostMapping("/search")
    public String search(@RequestParam("condCode") String condCode,
                              @RequestParam("inputVal") String inputVal,
                              Model model)
    {
//        System.out.println(boardService.findAllByCond(condCode, inputVal));

//        model.addAttribute("boardResult", boardService.findAllByCond(condCode, inputVal));
//        model.addAttribute("boards", boardService.findAllByCond(condCode, inputVal));

//        return "/board/boardResult :: boardResultBox";
        List<Board> searchResult = boardService.findAllByCond(condCode, inputVal);
        model.addAttribute("boards", searchResult);
        return "/board/boardResult :: boardResultBox";
    }

    @GetMapping("/boardAll")
    public String getAllBoardsAPI(Model model) {
        model.addAttribute("boards", boardService.findAll());
        return "/board/boardResult :: boardResultBox"; // 전체 목록도 동일한 템플릿 조각 사용
    }

}