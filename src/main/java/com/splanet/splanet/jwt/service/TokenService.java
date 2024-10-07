package com.splanet.splanet.jwt.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.jwt.JwtTokenProvider;
import com.splanet.splanet.jwt.entity.RefreshToken;
import com.splanet.splanet.jwt.repository.RefreshTokenRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 604800000; // 7일

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public void storeRefreshToken(String refreshToken, Long userId) {
        RefreshToken tokenEntity = new RefreshToken(refreshToken, userId, REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS); // 7일
        refreshTokenRepository.save(tokenEntity);
    }

    public String regenerateAccessToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        Long userId = tokenEntity.getUserId();
        return jwtTokenProvider.createAccessToken(userId);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

}
