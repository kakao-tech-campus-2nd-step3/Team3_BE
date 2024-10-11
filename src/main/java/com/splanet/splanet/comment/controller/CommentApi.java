package com.splanet.splanet.comment.controller;

import com.splanet.splanet.comment.dto.CommentRequest;
import com.splanet.splanet.comment.dto.CommentResponse;
import com.splanet.splanet.core.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "댓글 관련 API")
public interface CommentApi {

    @GetMapping("/{userId}")
    @Operation(summary = "댓글 조회", description = "특정 사용자의 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 사용자에게 달린 댓글이 성공적으로 조회되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 유저 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "인증된 유저의 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "댓글을 조회할 유저의 ID", required = true) @PathVariable("userId") Long userIdPath);

    @PostMapping
    @Operation(summary = "댓글 작성", description = "댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 작성되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 유저 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> createComment(
            @Parameter(description = "인증된 유저의 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "댓글 작성 요청 정보", required = true) @RequestBody CommentRequest request);

    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 수정되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 댓글 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> updateComment(
            @Parameter(description = "인증된 유저의 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "수정할 댓글 ID", required = true) @PathVariable("commentId") Long commentId,
            @Parameter(description = "댓글 수정 요청 정보", required = true) @RequestBody CommentRequest request);

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 삭제되었습니다.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (유효하지 않은 댓글 ID).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> deleteComment(
            @Parameter(description = "인증된 유저의 ID", required = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "삭제할 댓글 ID", required = true) @PathVariable("commentId") Long commentId);
}
