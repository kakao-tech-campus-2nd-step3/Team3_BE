package com.splanet.splanet.user.service;

import com.splanet.splanet.user.dto.UserResponseDto;
import com.splanet.splanet.user.dto.UserUpdateRequestDto;
import com.splanet.splanet.user.entity.User;
import com.splanet.splanet.user.repository.UserRepository;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.splanet.splanet.core.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        return toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        User updatedUser = user.toBuilder()
                .nickname(requestDto.getNickname())
                .profileImage(requestDto.getProfileImage())
                .isPremium(requestDto.getIsPremium())
                .build();

        userRepository.save(updatedUser);

        return toUserResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDto createUser(String nickname, String profileImage, Boolean isPremium) {
        Long testKakaoId = System.currentTimeMillis();

        try {
            User newUser = User.builder()
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .isPremium(isPremium)
                    .kakaoId(testKakaoId)
                    .build();

            userRepository.save(newUser);

            return toUserResponseDto(newUser);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .isPremium(user.getIsPremium())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponseDto getUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        return toUserResponseDto(user);
    }
}
