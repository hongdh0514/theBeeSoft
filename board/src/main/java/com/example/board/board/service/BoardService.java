package com.example.board.board.service;

import com.example.board.board.domain.Board;
import com.example.board.board.repository.BoardRepository;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok이 자동으로 생성자 추가
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public void deleteById(Long id) {
        boardRepository.deleteById(id);
    }

    public String checkBoard(Long boardId, User loginUser, Model model, HttpSession session){

//        게시글 존재 유무 체크
        Optional<Board> board = findById(boardId);
        if(board.isEmpty()) {
            session.setAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "redirect:/board";
        }

//        작성자 운영자 체크
        String loginUserId = loginUser.getUserId();
        if(loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            model.addAttribute("board", board.get());
            return "board/detail";
        }
        else {
            session.setAttribute("errorMessage", "작성자 혹은 운영자만 조회할 수 있습니다.");
            return "redirect:/board";
        }
    }

//    api 호출용
    public String checkBoardApi(Long boardId, User loginUser) {
        if (loginUser == null) {
            return "failure_no_login_user";
        }

        Optional<Board> board = findById(boardId);
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

    public String checkDelete(Long boardId, User loginUser) {
        if (loginUser == null) {
            return "failure_no_login_user";
        }

        Optional<Board> board = findById(boardId);
        if (board.isEmpty()) {
            return "failure_not_found";
        }

        String loginUserId = loginUser.getUserId();
        if (loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            deleteById(boardId);
            return "success";
        } else {
            return "failure_admin_or_writer";
        }
    }
}

