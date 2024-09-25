package com.splanet.splanet.user.repository;

import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  // 닉네임으로 사용자 찾기
  Optional<User> findByNickname(String nickname);
}