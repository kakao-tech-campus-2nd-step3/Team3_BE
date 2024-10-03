package com.splanet.splanet.oauth;

import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.service.UserService;
import com.splanet.splanet.oauth.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/issue")
    @Operation(summary = "테스트 유저 토큰 발급", description = "테스트용 유저에게 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰이 성공적으로 발급되었습니다.",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류.", content = @Content)
    })
    public ResponseEntity<String> issueToken(@RequestParam Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        String token = jwtTokenProvider.createToken(user.getId(), user.getNickname());
        return ResponseEntity.ok(token);
    }
}
