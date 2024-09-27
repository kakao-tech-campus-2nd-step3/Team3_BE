package com.splanet.splanet.user.service;

import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 유저_조회_성공() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .nickname("라이언")
                .profileImage("profile.png")
                .isPremium(true)
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponseDto result = userService.getUserById(userId);

        // then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getNickname()).isEqualTo("라이언");
        assertThat(result.getProfileImage()).isEqualTo("profile.png");
        assertThat(result.getIsPremium()).isTrue();
    }

    @Test
    void 유저_조회_없는유저() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 유저_정보_수정_성공() {
        // given
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .nickname("라이언")
                .profileImage("oldProfile.png")
                .isPremium(false)
                .build();

        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                .nickname("춘식이")
                .profileImage("newProfile.png")
                .isPremium(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        // when
        UserResponseDto updatedUser = userService.updateUserInfo(userId, requestDto);

        // then
        assertThat(updatedUser.getNickname()).isEqualTo("춘식이");
        assertThat(updatedUser.getProfileImage()).isEqualTo("newProfile.png");
        assertThat(updatedUser.getIsPremium()).isTrue();

        // verify that save method was called
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 유저_정보_수정_없는유저() {
        // given
        Long userId = 1L;
        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                .nickname("춘식이")
                .profileImage("newProfile.png")
                .isPremium(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserInfo(userId, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 유저_삭제_성공() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .nickname("라이언")
                .profileImage("profile.png")
                .isPremium(true)
                .build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    void 유저_삭제_없는유저() {
        // given
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
