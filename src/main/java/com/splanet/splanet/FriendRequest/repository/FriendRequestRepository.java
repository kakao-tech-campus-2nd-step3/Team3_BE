package com.splanet.splanet.FriendRequest.repository;

import com.splanet.splanet.FriendRequest.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
}