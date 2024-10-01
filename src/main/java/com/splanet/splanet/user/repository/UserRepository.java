package com.splanet.splanet.user.repository;

import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByNickname(String nickname);
  Optional<User> findByKakaoId(Long kakaoId);

}