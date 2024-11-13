package com.splanet.splanet.notification.repository;

import com.splanet.splanet.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByUserIdAndToken(Long userId, String token);
    List<FcmToken> findByUserId(Long userId);
    List<FcmToken> findByUserIdIn(Collection<Long> userIds);
    Optional<FcmToken> findByToken(String token);
}
