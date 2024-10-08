package com.splanet.splanet.comment.dto;

import com.splanet.splanet.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private Long writerId;
    private String writerNickname;
    private String writerProfileImage;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writerId(comment.getWriter().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .writerNickname(comment.getWriter().getNickname())
                .writerProfileImage(comment.getWriter().getProfileImage())
                .build();
    }
}