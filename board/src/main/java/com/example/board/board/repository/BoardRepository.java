package com.example.board.board.repository;

import com.example.board.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Board> findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(String title, String categoryName, Pageable pageable);
    Page<Board> findByContentContainingIgnoreCase(String content, Pageable pageable);
}