package com.splanet.splanet.comment.controller;

import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.comment.service.CommentService;
import com.splanet.splanet.core.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{user_id}")
    @Operation(summary = "댓글 조회", description = "특정 사용자의 댓글 목록을 조회 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "특정 사용자에게 달린 댓글이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 친구 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CommentResponse>> getComments(
            @AuthenticationPrincipal Long userId,
            @PathVariable("user_id") Long userIdPath) {
        List<CommentResponse> comments = commentService.getComments(userIdPath);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 작성되었습니다."),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 유저 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> createComment(
            @AuthenticationPrincipal Long userId,
            @RequestBody CommentRequest request) {
        commentService.createComment(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 작성되었습니다.");
    }

    @PutMapping("/{comment_id}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "댓글이 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 댓글 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> updateComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRequest request) {
        commentService.updateComment(commentId, request, userId);
        return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{comment_id}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "댓글이 성공적으로 삭제되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청입니다 (유효하지 않은 댓글 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }
}