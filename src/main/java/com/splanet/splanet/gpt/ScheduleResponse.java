package com.splanet.splanet.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private List<Schedule> schedules; // 스케줄 리스트

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Schedule {
        private String concept; // 스케줄 컨셉
        private List<Task> tasks; // 업무 리스트

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Task {
            private String task; // 업무명
            private String duration; // 소요 시간
            private int priority; // 우선순위
            private String startTime; // 시작 시간
            private String endTime; // 종료 시간
        }
    }
}