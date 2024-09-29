package com.splanet.splanet.team.service;

import com.splanet.splanet.team.entity.Team;
import com.splanet.splanet.team.repository.TeamRepository;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Team createTeam(String teamName) {
    Team team = Team.builder()
            .teamName(teamName)
            .build();
    return teamRepository.save(team);
  }

  @Transactional(readOnly = true)
  public Optional<Team> getTeamById(Long teamId) {
    return teamRepository.findById(teamId);
  }

  @Transactional(readOnly = true)
  public List<Team> getAllTeams() {
    return teamRepository.findAll();
  }

  @Transactional
  public Team updateTeam(Long teamId, String newTeamName) {
    Team existingTeam = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

    Team updatedTeam = Team.builder()
            .id(existingTeam.getId())
            .teamName(newTeamName) // 수정된 팀 이름 설정
            .createdAt(existingTeam.getCreatedAt())
            .updatedAt(existingTeam.getUpdatedAt())
            .user(existingTeam.getUser())
            .users(existingTeam.getUsers())
            .deleted(existingTeam.getDeleted())
            .build();

    return teamRepository.save(updatedTeam);
  }

  @Transactional
  public void deleteTeam(Long teamId) {
    teamRepository.deleteById(teamId);
  }

  @Transactional
  public Team addUserToTeam(Long teamId, Long userId) {
    Team existingTeam = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

    List<User> updatedUsers = existingTeam.getUsers();
    updatedUsers.add(user);

    Team updatedTeam = Team.builder()
            .id(existingTeam.getId())
            .teamName(existingTeam.getTeamName())
            .createdAt(existingTeam.getCreatedAt())
            .updatedAt(existingTeam.getUpdatedAt())
            .user(existingTeam.getUser())
            .users(updatedUsers)
            .deleted(existingTeam.getDeleted())
            .build();

    return teamRepository.save(updatedTeam);
  }

  @Transactional
  public Team removeUserFromTeam(Long teamId, Long userId) {
    Team existingTeam = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

    List<User> updatedUsers = existingTeam.getUsers();
    updatedUsers.remove(user);

    Team updatedTeam = Team.builder()
            .id(existingTeam.getId())
            .teamName(existingTeam.getTeamName())
            .createdAt(existingTeam.getCreatedAt())
            .updatedAt(existingTeam.getUpdatedAt())
            .user(existingTeam.getUser())
            .users(updatedUsers)
            .deleted(existingTeam.getDeleted())
            .build();

    return teamRepository.save(updatedTeam);
  }
}