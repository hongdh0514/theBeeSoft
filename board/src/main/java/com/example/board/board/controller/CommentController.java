package com.example.board.board.controller;

import com.example.board.board.domain.Board;
import com.example.board.board.domain.Comment;
import com.example.board.board.domain.CommentRequestDto;
import com.example.board.board.domain.CommentResponseDTO;
import com.example.board.board.service.BoardService;
import com.example.board.board.service.CommentService;
import com.example.board.user.domain.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/board/{boardId}/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

//    댓글 작성 기존
//    @PostMapping("/save")
//    @ResponseBody
//    public String save(
//            @RequestParam Long boardId,
//            @RequestParam String content,
//            @RequestParam String loginUser,
//            Comment comment) {
//
//        Optional<Board> boardOptional = boardService.findById(boardId);
//
//        if (boardOptional.isPresent()) {
//            Board board = boardOptional.get();
//
//            comment.setBoard(board);
//            comment.setContent(content);
//            comment.setWriter(loginUser);
//
//            System.out.println("comment : " + commentService.save(comment));
//
//            return "success";
//        }
//        return "no_board_found";
//    }

    @PostMapping("/save")
    public ResponseEntity<?> saveComment(@PathVariable Long boardId, @RequestBody CommentRequestDto commentDto) {

        System.out.println("board id : " + boardId);

//        board check
        Optional<Board> boardOptional = boardService.findById(boardId);
        if (boardOptional.isEmpty()) {
            System.out.println("board not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no_board_found");
        }

        Board board = boardOptional.get();

        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent(commentDto.getContent());
        comment.setWriter(commentDto.getWriter());

        if (commentDto.getParentId() != null) {
//            부모 댓글 check
            Optional<Comment> parentCommentOptional = commentService.findById(commentDto.getParentId());
            if (parentCommentOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no_parent_comment_found");
            }
            comment.setParentComment(parentCommentOptional.get());
        }

//        댓글 저장
        Comment savedComment = commentService.save(comment);

        CommentResponseDTO responseDto = new CommentResponseDTO(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getWriter(),
                savedComment.getCreatedAt(),
                savedComment.getBoard().getId(),
                savedComment.getParentComment() != null ? savedComment.getParentComment().getId() : null
        );

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId,  HttpSession session) {
        Optional<Comment> commentOptional = commentService.findById(commentId);
        if (commentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no_comment_found");
        }
        Comment commentToDelete = commentOptional.get();

//        게시글 id와 댓글의 게시글 id 확인
        if (!commentToDelete.getBoard().getId().equals(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("wrong_board_id");
        }


//        권한 확인 (login user)
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
        if (!loginUser.getUserId().toLowerCase().contains("admin") || !loginUser.getUserId().equals(commentToDelete.getWriter())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }

        try {
            int deletedCount = commentService.deleteComment(commentId);
            if (deletedCount > 0) {
                return ResponseEntity.ok(deletedCount);
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("count_zero_comment");
            }
        } catch (Exception e) {
            System.err.println("댓글 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("delete_comment_error : " + e.getMessage());
        }
    }
}
