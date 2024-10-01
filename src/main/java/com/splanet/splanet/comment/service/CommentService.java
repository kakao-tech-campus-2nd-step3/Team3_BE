package com.splanet.splanet.comment.service;

import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.entity.Comment;
import com.splanet.splanet.comment.repository.CommentRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    // 댓글 조회
    public List<CommentResponse> getComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        return CommentResponse.fromCommentList(comments);
    }

    // 댓글 작성
    @Transactional
    public void createComment(Long userId, CommentRequest request) {
        Comment comment = Comment.builder()
                .userId(request.getUserId())
                .writerId(userId)
                .content(request.getContent())
                .build();
        commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자 확인
        if (!comment.getWriterId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자 확인
        if (!comment.getWriterId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}