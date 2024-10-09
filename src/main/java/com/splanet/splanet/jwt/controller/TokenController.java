package com.splanet.splanet.jwt.controller;

import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.jwt.service.TokenService;
import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController implements TokenApi {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Override
    public ResponseEntity<String> issueToken(Long userId) {
        UserResponseDto user = userService.getUserById(userId);
        String token = jwtTokenProvider.createAccessToken(user.getId());
        return ResponseEntity.ok(token);
    }

    @Override
    public ResponseEntity<String> refreshAccessToken(String refreshToken, String deviceId) {
        String newAccessToken = tokenService.regenerateAccessToken(refreshToken, deviceId);
        return ResponseEntity.ok(newAccessToken);
    }

    @Override
    public ResponseEntity<Void> deleteRefreshToken(Long userId, String deviceId) {
        tokenService.deleteRefreshToken(userId, deviceId);
        return ResponseEntity.ok().build();
    }
}
