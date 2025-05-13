package com.example.board.board.repository;

import com.example.board.board.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("SELECT c FROM Comment c WHERE c.board.id = :id ORDER BY c.createdAt DESC")
    List<Comment> findByBoardIdOrderByCreatedAtDesc(@Param("id") Long id);

    @EntityGraph(attributePaths = {"parentComment", "replies"}) // parentComment와 replies 컬렉션을 EAGER로 가져오도록 지시
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.createdAt DESC")
    List<Comment> findAllByBoardIdWithHierarchy(@Param("boardId") Long boardId);

    @Query("SELECT c.board.id, COUNT(c) FROM Comment c WHERE c.board.id IN :boardIdList GROUP BY c.board.id")
    List<Object[]> countByBoardIdIn(List<Long> boardIdList);

    long countByBoardId(Long boardId);
}
