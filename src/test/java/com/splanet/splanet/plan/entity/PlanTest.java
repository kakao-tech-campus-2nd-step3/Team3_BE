package com.splanet.splanet.plan.entity;

import com.splanet.splanet.user.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PlanTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 플랜_유효성_성공() {
        // given
        User user = new User(); // assuming a simple user entity
        Plan plan = Plan.builder()
                .user(user)
                .title("유효한 제목")
                .description("유효한 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Set<ConstraintViolation<Plan>> violations = validator.validate(plan);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void 플랜_제목_빈값_실패() {
        // given
        User user = new User(); // assuming a simple user entity
        Plan plan = Plan.builder()
                .user(user)
                .title("")  // invalid empty title
                .description("유효한 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Set<ConstraintViolation<Plan>> violations = validator.validate(plan);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 공백일 수 없습니다."));
    }

    @Test
    void 플랜_제목_길이초과_실패() {
        // given
        User user = new User(); // assuming a simple user entity
        Plan plan = Plan.builder()
                .user(user)
                .title("a".repeat(101))  // title exceeds max length
                .description("유효한 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Set<ConstraintViolation<Plan>> violations = validator.validate(plan);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("제목은 100자를 넘을 수 없습니다."));
    }

    @Test
    void 플랜_설명_길이초과_실패() {
        // given
        User user = new User(); // assuming a simple user entity
        Plan plan = Plan.builder()
                .user(user)
                .title("유효한 제목")
                .description("a".repeat(1001))  // description exceeds max length
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Set<ConstraintViolation<Plan>> violations = validator.validate(plan);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("설명은 1000자를 넘을 수 없습니다."));
    }

    @Test
    void 플랜_유저_필수_실패() {
        // given
        Plan plan = Plan.builder()
                .title("유효한 제목")
                .description("유효한 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // when
        Set<ConstraintViolation<Plan>> violations = validator.validate(plan);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("유저는 필수입니다."));
    }
}
