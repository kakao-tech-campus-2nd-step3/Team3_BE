package com.splanet.splanet.comment.controller;

import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    public ResponseEntity<List<CommentResponse>> getComments(Long userId, Long userIdPath) {
        List<CommentResponse> comments = commentService.getComments(userIdPath);
        return ResponseEntity.ok(comments);
    }

    @Override
    public ResponseEntity<String> createComment(Long writerId, Long userId, CommentRequest request) {
        commentService.createComment(writerId, userId, request);
        return ResponseEntity.ok("댓글이 성공적으로 작성되었습니다.");
    }

    @Override
    public ResponseEntity<String> updateComment(Long userId, Long commentId, CommentRequest request) {
        commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
    }

    @Override
    public ResponseEntity<String> deleteComment(Long userId, Long commentId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }
}
