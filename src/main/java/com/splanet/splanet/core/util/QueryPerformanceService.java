package com.splanet.splanet.core.util;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryPerformanceService {

    private final EntityManagerFactory entityManagerFactory;

    public void measureQueryCountAndTime(Runnable methodToTest) {
        // SessionFactory에서 Statistics 객체 가져오기
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.clear();  // 이전 통계 초기화

        // 쿼리 실행 시간 측정 시작
        long startTime = System.nanoTime();

        // 테스트할 메서드 실행
        methodToTest.run();

        // 쿼리 실행 시간 측정 종료
        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1_000_000; // 밀리초로 변환

        // 쿼리 실행 횟수 확인
        long queryCount = statistics.getQueryExecutionCount();

        // 실행 시간과 쿼리 횟수 로그 출력
        System.out.println("Query Execution Time: " + executionTime + " ms");
        System.out.println("Query Execution Count: " + queryCount);
    }
}
