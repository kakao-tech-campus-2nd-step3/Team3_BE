# Splanet
![image](https://github.com/user-attachments/assets/c7f7b8ee-0764-404c-b547-e67dc5c23b77)

### Speak and Plan It! 내 목소리로 만들어지는 나만의 플래너

---

# 목차

- [Splanet](#splanet)
  - [1. 프로젝트 개요](#1-프로젝트-개요)
    - [API 명세서](#api-명세서)
    - [ERD](#erd)
  - [2. 기술 스택](#2-기술-스택)
    - [프론트엔드](#프론트엔드)
    - [백엔드](#백엔드)
    - [데이터베이스](#데이터베이스)
    - [인프라](#인프라)
  - [3. 주요 기능](#3-주요-기능)
    - [카카오 로그인](#카카오-로그인)
    - [실시간 음성 인식](#실시간-음성-인식)
    - [플랜 자동 생성](#플랜-자동-생성)
    - [플랜 관리](#플랜-관리)
    - [친구](#친구)
  - [4. 프로젝트 구조](#4-프로젝트-구조)
  - [7. 개발 관련](#7-개발-관련)


## 1. 프로젝트 개요

계획 짜는 것 마저 계획인 당신에게 선사합니다. 

### API 명세서

### ERD
![image](https://github.com/user-attachments/assets/40ca0599-f873-4b6d-b970-bd3858e7e86d)


---

## 2. 기술 스택

### 프론트엔드

![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)

### 백엔드

![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

### 데이터베이스

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### 인프라

![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)


---

## 3. 주요 기능

### 카카오 로그인

| ![카카오 로그인](https://github.com/user-attachments/assets/3883651a-f1a0-43f9-8272-70645035adc6) | 카카오 ID를 통해 사용자를 인증합니다. |
|:---:|---|

### 실시간 음성 인식

| ![실시간 음성 인식](https://github.com/user-attachments/assets/66d14a1b-0396-4ca5-b16e-8574427e4319) | 실시간 음성 인식을 통해 사용자의 요구사항을 작성합니다. |
|:---:|---|

### 플랜 자동 생성

| ![플랜 자동 생성](https://github.com/user-attachments/assets/66d14a1b-0396-4ca5-b16e-8574427e4319) | 사용자의 요구사항에 맞게 플랜을 자동으로 생성합니다. (fine-tuning 모델 이용) 추천된 3개 중 하나를 선택할 수 있습니다. |
|:---:|---|

### 플랜 관리

| ![플랜 관리](https://github.com/user-attachments/assets/7c491439-0b9e-442b-a327-48f9be6b6604) | 메인 페이지에서 본인의 플랜을 관리할 수 있습니다. 드래그 앤 드롭 및 카드 크기 조절을 통해 플랜을 변경할 수 있습니다. |
|:---:|---|

### 친구

| ![친구 추가](https://github.com/user-attachments/assets/d82d9137-5250-443f-9013-d7877915603a) | 다른 사용자와 친구를 맺을 수 있으며, 친구의 플랜을 볼 수 있습니다. |
|:---:|---|

| ![친구 플랜 보기](https://github.com/user-attachments/assets/00890584-023c-4904-b4b7-d480729f02dd) | 친구의 플랜을 볼 수 있습니다. |
|:---:|---|

| ![댓글 작성](https://github.com/user-attachments/assets/f3cd44ea-9018-41d4-942a-8adf2bce6ba2) | 친구에게 댓글을 작성할 수 있습니다. |
|:---:|---|

---

## 4. 프로젝트 구조

```
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂splanet
 ┃ ┃ ┃ ┃ ┗ 📂splanet
 ┃ ┃ ┃ ┃ ┃ ┣ 📂comment
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CommentApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CommentRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Comment.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FirebaseConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜OpenAiConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RedisConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SwaggerConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WebSocketConfig.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂core
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BusinessException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResponseConstants.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂fcm
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FCMInitializer.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SpeechWebSocketHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂properties
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ClovaProperties.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GptProperties.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtProperties.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OAuth2Properties.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂util
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜QueryPerformanceService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂friend
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FriendApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Friend.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂friendRequest
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FriendRequestApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRequestController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FriendRequestCreateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ReceivedFriendRequestResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SentFriendRequestResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SuccessResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRequestRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRequestService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂gpt
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GptApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GptController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GptPlanSaveController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DeviceIdGenerator.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GptPlanSaveService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GptService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜GptRequest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂jwt
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TokenController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RefreshToken.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RefreshTokenRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TokenService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAuthenticationFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JwtTokenProvider.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂notification
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmTokenApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmTokenController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜NotificationController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmTokenRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FcmTokenUpdateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmToken.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜NotificationLog.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmTokenRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜NotificationLogRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂scheduler
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜NotificationScheduler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜FcmTokenService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜NotificationService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂oauth
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomOAuth2UserService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜OAuth2AuthenticationSuccessHandler.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂payment
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PaymentRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Payment.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂plan
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanRequestDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanResponseDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Plan.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanMapper.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂previewplan
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PreviewPlanApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PreviewPlanController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanCardRequestDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanCardResponseDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanGroupRequestDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanGroupWithCardsResponseDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanCard.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanGroup.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PreviewPlan.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanCardRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlanGroupRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PreviewPlanRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PreviewPlanService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂stt
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ClovaSpeechGrpcService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ClovaSpeechService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂subscription
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SubscriptionApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SubscriptionController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SubscriptionRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SubscriptionResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Subscription.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SubscriptionRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SubscriptionService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂team
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamInvitationDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamMemberDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜InvitationStatus.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Team.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamInvitation.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamUserRelation.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserTeamRole.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamInvitationRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamUserRelationRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂teamplan
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamPlanApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlanController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TeamPlanRequestDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlanResponseDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlan.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlanMapper.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlanRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamPlanService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserApi.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserResponseDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserUpdateRequestDto.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜User.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserService.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜SplanetApplication.java
 ┃ ┣ 📂proto
 ┃ ┃ ┗ 📜nest.proto
 ┃ ┣ 📂resources
 ┃ ┃ ┣ 📂static
 ┃ ┃ ┣ 📜.DS_Store
 ┃ ┃ ┣ 📜application-local.yml
 ┃ ┃ ┣ 📜application-prod.yml
 ┃ ┃ ┣ 📜application.yml
 ┃ ┃ ┣ 📜env.properties
 ┃ ┃ ┗ 📜splanet-firebase.json
 ┃ ┗ 📜.DS_Store
 ┣ 📂test
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂splanet
 ┃ ┃ ┃ ┃ ┗ 📂splanet
 ┃ ┃ ┃ ┃ ┃ ┣ 📂comment
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommentServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂friend
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂friendRequest
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜FriendRequestServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂payment
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PaymentServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂plan
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlanServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂previewplan
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PreviewPlanServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂team
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TeamServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserControllerAcceptanceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserControllerIntegrationTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜SplanetApplicationTests.java
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📜application-test.yml
 ┃ ┃ ┗ 📜application.yml
 ┗ 📜.DS_Store
```


# 7. 개발 관련
[웹 푸시 알림 - 스케줄링 쿼리 해소](https://kanguk-room.notion.site/12c036cad7a88073b0a0e1098775c723?pvs=4)
[CLOVA Speech 실시간 스트리밍](https://kanguk-room.notion.site/STT-CLOVA-Speech-API-123036cad7a88098b644c957f8420080?pvs=4)
[Redis 사용](https://medium.com/@kanguk.ku/redis-%EC%82%AC%EC%9A%A9%EA%B8%B0-4fd3695ab0c7)
[무중단 배포](https://kanguk-room.notion.site/132036cad7a880e68d7bd8846b25f8a6?pvs=4)

