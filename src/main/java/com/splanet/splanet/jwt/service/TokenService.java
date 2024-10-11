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

    public void storeRefreshToken(String refreshToken, Long userId, String deviceId) {
        String key = userId + ":" + deviceId;
        RefreshToken tokenEntity = new RefreshToken(key, refreshToken, REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS); // 7일
        refreshTokenRepository.save(tokenEntity);
    }

    public String regenerateAccessToken(String refreshToken, String deviceId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String key = userId + ":" + deviceId;
        RefreshToken tokenEntity = refreshTokenRepository.findById(key)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        if (!tokenEntity.getToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return jwtTokenProvider.createAccessToken(userId);
    }

    public void deleteRefreshToken(Long userId, String deviceId) {
        String key = userId + ":" + deviceId;
        refreshTokenRepository.deleteById(key);
    }

}
