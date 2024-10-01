package com.splanet.splanet.comment.dto;

import com.splanet.splanet.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CommentResponse> fromCommentList(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .writerId(comment.getWriterId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .writerNickname("작성자 닉네임")
                        .writerProfileImage("프로필 이미지 URL")
                        .build())
                .collect(Collectors.toList());
    }
}
