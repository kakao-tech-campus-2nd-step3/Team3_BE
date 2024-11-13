// src/main/java/com/splanet/splanet/notification/service/FcmTokenService.java
package com.splanet.splanet.notification.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.notification.dto.FcmTokenUpdateRequest;
import com.splanet.splanet.notification.entity.FcmToken;
import com.splanet.splanet.notification.repository.FcmTokenRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerFcmToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FcmToken fcmToken = fcmTokenRepository.findByUserIdAndToken(userId, token)
                .orElse(FcmToken.builder()
                        .user(user)
                        .token(token)
                        .build());

        fcmTokenRepository.save(fcmToken);
    }

    @Transactional
    public void updateFcmTokenSettings(FcmTokenUpdateRequest request) {
        FcmToken fcmToken = fcmTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));

        fcmToken = fcmToken.toBuilder()
                .isNotificationEnabled(request.isNotificationEnabled() != null
                        ? request.isNotificationEnabled()
                        : fcmToken.getIsNotificationEnabled())
                .notificationOffset(request.notificationOffset() != null
                        ? request.notificationOffset()
                        : fcmToken.getNotificationOffset())
                .build();

        fcmTokenRepository.save(fcmToken);
    }

    @Transactional
    public void deleteFcmToken(String token) {
        FcmToken fcmToken = fcmTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_NOT_FOUND));
        fcmTokenRepository.delete(fcmToken);
    }
}
