package com.splanet.splanet.user.repository;

import com.splanet.splanet.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
