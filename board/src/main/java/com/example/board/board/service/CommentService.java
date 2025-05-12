package com.example.board.board.service;

import com.example.board.board.domain.Comment;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> findByBoardId(Long id) {
        return commentRepository.findByBoardIdOrderByCreatedAtDesc(id);
    }

    public Map<Long, Long> getCommentCount(List<Long> boardIdList) {
        return commentRepository.countByBoardIdIn(boardIdList).stream().collect(Collectors.toMap(result -> (Long) result[0], result -> (Long) result[1]));
    }
}
