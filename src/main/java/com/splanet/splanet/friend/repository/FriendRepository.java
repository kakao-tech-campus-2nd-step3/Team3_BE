package com.splanet.splanet.friend.repository;

import com.splanet.splanet.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserId(Long userId);
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
}