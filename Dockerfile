# 빌드 단계
FROM eclipse-temurin:21 AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew

# Gradle 빌드에서 프로필을 지정하여 실행
RUN ./gradlew bootJar -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}

# 런타임 단계
FROM eclipse-temurin:21
COPY --from=builder build/libs/*.jar app.jar

# 런타임에서도 동일하게 환경 변수 사용
ENV SPRING_PROFILES_ACTIVE=prod


ENTRYPOINT ["java", "-jar", "/app.jar"]
VOLUME /tmp