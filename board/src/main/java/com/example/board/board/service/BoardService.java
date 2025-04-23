package com.example.board.board.service;

import com.example.board.board.domain.Board;
import com.example.board.board.domain.Category;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CategoryRepository;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok이 자동으로 생성자 추가
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

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

    public String checkBoard(Long boardId, User loginUser){

//        게시글 존재 유무 체크
        Optional<Board> board = findById(boardId);
        if(board.isEmpty()) {
            return "failure_not_found";
        }

//        작성자 운영자 체크
        String loginUserId = loginUser.getUserId();
        if(loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            return "success";
        }
        else {
            return "failure_admin_or_writer";
        }
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Board> findAllByCond(String condCode, String inputVal) {

        return switch (condCode) {
            case "1" -> boardRepository.findByTitleContainingIgnoreCase(inputVal);
            case "2" -> boardRepository.findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(inputVal, inputVal);
            case "3" -> boardRepository.findByContentContainingIgnoreCase(inputVal);
            default -> null;
        };
    }
}

