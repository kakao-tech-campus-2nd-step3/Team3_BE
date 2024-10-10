package com.splanet.splanet.comment.dto;

import com.splanet.splanet.comment.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long id,
        Long userId,
        Long writerId,
        String writerNickname,
        String writerProfileImage,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CommentResponse fromComment(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getWriter().getId(),
                comment.getWriter().getNickname(),
                comment.getWriter().getProfileImage(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}