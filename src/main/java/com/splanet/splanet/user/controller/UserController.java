package com.splanet.splanet.user.controller;

import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseDto> getUserInfo(Long userId) {
        UserResponseDto userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUserInfo(Long userId, UserUpdateRequestDto requestDto) {
        UserResponseDto updatedUser = userService.updateUserInfo(userId, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserResponseDto> createUser(String nickname, String profileImage, Boolean isPremium) {
        UserResponseDto newUser = userService.createUser(nickname, profileImage, isPremium);
        return ResponseEntity.status(201).body(newUser);
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserByNickname(@PathVariable("user_nickname") String userNickname) {
        UserResponseDto userResponse = userService.getUserByNickname(userNickname);
        return ResponseEntity.ok(userResponse);
    }
}
