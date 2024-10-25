package com.splanet.splanet.friendRequest.repository;

import com.splanet.splanet.friendRequest.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.requester WHERE fr.receiver.id = :userId")
    List<FriendRequest> findByReceiverIdWithRequester(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.receiver WHERE fr.requester.id = :userId")
    List<FriendRequest> findByRequesterIdWithReceiver(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :receiverId AND fr.requester.id = :requesterId AND fr.status = :status")
    List<FriendRequest> findPendingRequestsByReceiverId(@Param("receiverId") Long receiverId, @Param("requesterId") Long requesterId, @Param("status") FriendRequest.Status status);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.receiver.id = :userId")
    List<FriendRequest> findByReceiverId(@Param("userId") Long userId);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.requester.id = :userId")
    List<FriendRequest> findByRequesterId(@Param("userId") Long userId);
}