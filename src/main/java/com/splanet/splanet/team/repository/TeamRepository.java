package com.splanet.splanet.team.repository;

import com.splanet.splanet.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
