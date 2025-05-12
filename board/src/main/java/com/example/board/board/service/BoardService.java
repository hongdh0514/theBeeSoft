package com.example.board.board.service;

import com.example.board.board.domain.Board;
import com.example.board.board.domain.Category;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CategoryRepository;
import com.example.board.board.repository.CommentRepository;
import com.example.board.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;

    public Page<Board> findAll(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return boardRepository.findAll(pageable);
    }

    public List<Long> getBoardId(int page) {
        Page<Board> boardPage = findAll(page);
        return boardPage.getContent().stream()
                .map(Board::getId)
                .collect(Collectors.toList());
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    @Transactional
    public void deleteById(Long id) {
//        댓글 조회 후 있으면 삭제를 해야하나
        commentRepository.deleteByBoardId(id);
        boardRepository.deleteById(id);
    }

    public String checkBoard(Long boardId, User loginUser) {
        Optional<Board> board = findById(boardId);
        if(board.isEmpty()) {
            return "failure_not_found";
        }

        String loginUserId = loginUser.getUserId();
        if(loginUserId.toLowerCase().contains("admin") || board.get().getWriter().equals(loginUserId)) {
            return "success";
        } else {
            return "failure_admin_or_writer";
        }
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Board> findAllByCond(String condCode, String inputVal, int page) {
//        Pageable pageable = PageRequest.of(page, 10);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return switch (condCode) {
            case "1" -> boardRepository.findByTitleContainingIgnoreCase(inputVal, pageable);
            case "2" -> boardRepository.findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(inputVal, inputVal, pageable);
            case "3" -> boardRepository.findByContentContainingIgnoreCase(inputVal, pageable);
            default -> Page.empty();
        };
    }

    public List<Long> getBoardIdsFromPage(Page<Board> boardPage) {
        return boardPage.getContent().stream()
                .map(Board::getId)
                .collect(Collectors.toList());
    }
}