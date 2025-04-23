package com.example.board.board.repository;

import com.example.board.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목에 검색어가 포함된 게시글 조회
    List<Board> findByTitleContainingIgnoreCase(String searchText);

    // 제목 또는 카테고리 이름에 검색어가 포함된 게시글 조회
    List<Board> findByTitleContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(String title, String categoryName);

    // 내용에 검색어가 포함된 게시글 조회
    List<Board> findByContentContainingIgnoreCase(String searchText);
}