package com.example.board.board.service;

import com.example.board.board.domain.Comment;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> findByBoardId(Long id) {
        return commentRepository.findByBoardIdOrderByCreatedAtDesc(id);
    }

    public Map<Long, Long> getCommentCount(List<Long> boardIdList) {
        return commentRepository.countByBoardIdIn(boardIdList).stream().collect(Collectors.toMap(result -> (Long) result[0], result -> (Long) result[1]));
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentByBoardId(Long boardId) {
        // 1. 게시글에 속한 모든 댓글 가져오기
        List<Comment> allComments = commentRepository.findAllByBoardIdWithHierarchy(boardId);

        // 2. 댓글 id로 Map 만들기 (여기서 Set<Long>을 사용하여 중복 방지)
        Map<Long, Comment> commentMap = new HashMap<>();
        for (Comment comment : allComments) {
            // Map에 없는 키만 추가
            if (!commentMap.containsKey(comment.getId())) {
                commentMap.put(comment.getId(), comment);
            }
        }

        // 3. 계층 구조 재구성
        List<Comment> rootComments = new ArrayList<>();
        Set<Long> processedCommentIds = new HashSet<>(); // 이미 처리된 댓글 ID 추적

        for (Comment comment : allComments) {
            if (processedCommentIds.contains(comment.getId())) {
                continue; // 이미 처리된 댓글이면 건너뛰기
            }

            Comment currentComment = commentMap.get(comment.getId());
            if (currentComment == null) continue; // 맵에 없으면 건너뛰기

            if (currentComment.getParentComment() == null) {
                rootComments.add(currentComment);
            }
            else {
                Comment parent = commentMap.get(currentComment.getParentComment().getId());
                if (parent != null) {
                    if (!parent.getReplies().contains(currentComment)) {
                        parent.getReplies().add(currentComment);
                    }
                }
            }
            processedCommentIds.add(currentComment.getId());
        }

        // 4. 루트 댓글과 각 댓글의 답글들 정렬 (최신순)
        rootComments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        for (Comment rootComment : rootComments) {
            rootComment.getReplies().sort(Comparator.comparing(Comment::getCreatedAt).reversed());
        }

        return rootComments;
    }

//    댓글 카운트
    public long getTotalCommentCount(Long boardId) {
        return commentRepository.countByBoardId(boardId); // CommentRepository에 이 메서드를 추가해야 합니다.
    }

    public int deleteComment(Long commentId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            return 0; // 삭제할 댓글이 없으면 0 리턴
        }

        Comment commentToDelete = commentOptional.get();
        int deletedCount = 0;

        deletedCount = 1 + getCountDeletedComment(commentToDelete); // 자신 포함

        commentRepository.deleteById(commentId); // 부모 댓글 삭제 (자식까지 연쇄 삭제)
        return deletedCount;
    }

    private int getCountDeletedComment(Comment comment) {
        if (comment.getReplies() == null || comment.getReplies().isEmpty()) {
            return 0;
        }
        int count = comment.getReplies().size();
        for (Comment reply : comment.getReplies()) {
            count += getCountDeletedComment(reply);
        }
        return count;
    }
}
