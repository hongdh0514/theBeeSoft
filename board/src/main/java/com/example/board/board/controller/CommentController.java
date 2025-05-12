package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.domain.Comment;
import com.example.board.board.service.BoardService;
import com.example.board.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

//    댓글 작성
    @PostMapping("/save")
    @ResponseBody
    public String save(
            @RequestParam Long boardId,
            @RequestParam String content,
            @RequestParam String loginUser,
            Comment comment) {

        Optional<Board> boardOptional = boardService.findById(boardId);

        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();

            comment.setBoard(board);
            comment.setContent(content);
            comment.setWriter(loginUser);

            System.out.println("comment : " + commentService.save(comment));

            return "success";
        }
        return "no_board_found";
    }
}
