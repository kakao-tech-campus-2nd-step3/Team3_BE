package com.splanet.splanet.friendRequest.repository;

import com.splanet.splanet.friendRequest.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :userId")
    List<FriendRequest> findByReceiverId(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.requester.id = :userId")
    List<FriendRequest> findByRequesterId(@Param("userId") Long userId);
}