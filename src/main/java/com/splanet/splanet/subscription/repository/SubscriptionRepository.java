package com.splanet.splanet.subscription.repository;

import com.splanet.splanet.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // 주어진 사용자 ID 상태에 따라 구독을 찾아보기
    Optional<Subscription> findTopByUserIdAndStatusOrderByStartDateDesc(Long userId, Subscription.Status status);

    // 주어진 사용자 ID의 활성 구독이 존재하는지 확인
    boolean existsByUserIdAndStatus(Long userId, Subscription.Status status);

    // 사용자 ID에 대해 최신 활성 구독을 찾아보기
    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.status = 'ACTIVE' ORDER BY s.startDate DESC")
    Optional<Subscription> findLatestActiveSubscriptionByUserId(@Param("userId") Long userId);

    // 사용자 ID와 상태에 따라 구독 리스트 가져오기
    List<Subscription> findByUserIdAndStatus(Long userId, Subscription.Status status);
}