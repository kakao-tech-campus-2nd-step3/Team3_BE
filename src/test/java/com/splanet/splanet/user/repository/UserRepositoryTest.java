package com.splanet.splanet.user.repository;

import com.splanet.splanet.user.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @MockBean
    private UserRepository userRepository;

    @Test
    public void 닉네임으로_조회_성공() {
        // given
        User mockUser = User.builder()
                .id(1L)
                .nickname("닉네임")
                .kakaoId(123456789L)
                .build();

        // when
        Mockito.when(userRepository.findByNickname("닉네임")).thenReturn(Optional.of(mockUser));

        // then
        Optional<User> result = userRepository.findByNickname("닉네임");

        assertTrue(result.isPresent());
        assertEquals("닉네임", result.get().getNickname());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void 카카오id로_조회_성공() {
        // given
        User mockUser = User.builder()
                .id(1L)
                .nickname("닉네임")
                .kakaoId(123456789L)
                .build();

        // when
        Mockito.when(userRepository.findByKakaoId(123456789L)).thenReturn(Optional.of(mockUser));

        // then
        Optional<User> result = userRepository.findByKakaoId(123456789L);

        assertTrue(result.isPresent());
        assertEquals(123456789L, result.get().getKakaoId());
        assertEquals(1L, result.get().getId());
    }
}