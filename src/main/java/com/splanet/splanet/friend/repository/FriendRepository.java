package com.splanet.splanet.friend.repository;

import com.splanet.splanet.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserId(Long userId);
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    @Modifying
    @Query("DELETE FROM Friend f WHERE f.user.id = :requesterId AND f.friend.id = :receiverId")
    void deleteByRequesterIdAndReceiverId(@Param("requesterId") Long requesterId, @Param("receiverId") Long receiverId);
}