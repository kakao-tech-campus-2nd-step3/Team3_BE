package com.splanet.splanet.team.dto;

import com.splanet.splanet.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamDto {
  private Long id;
  private String teamName;
  private UserDto user;
  private List<UserDto> teamMembers;
}