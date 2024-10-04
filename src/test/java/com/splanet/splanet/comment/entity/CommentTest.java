package com.splanet.splanet.comment.entity;

import com.splanet.splanet.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CommentTest {

    private User mockUser;
    private User mockWriter;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockWriter = mock(User.class);

        comment = Comment.builder()
                .user(mockUser)
                .writer(mockWriter)
                .content("first content")
                .build();
    }

    @Test
    void 댓글_내용_수정_테스트() {
        // Given
        String updatedContent = "updated content";

        // When
        comment.updateContent(updatedContent);

        // Then
        assertThat(comment.getContent()).isEqualTo(updatedContent);
    }

    @Test
    void 댓글_최대_길이_테스트() {
        // Given
        String longContent = "a".repeat(256);

        // When
        comment.updateContent(longContent);

        // Then
        assertThat(comment.getContent().length()).isGreaterThan(255);
    }
}
