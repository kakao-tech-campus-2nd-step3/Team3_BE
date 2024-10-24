package com.splanet.splanet.gpt;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    private String schedulePeriod; // 스케줄 기간
    private List<String> taskList; // 업무 목록
    private Map<String, String> taskDurations; // 업무 소요 시간 (업무명: 소요시간)
    private Map<String, Integer> priority; // 업무 우선순위 (업무명: 우선순위)
    private List<String> scheduleConcepts; // 스케줄 컨셉 목록
    private List<String> timeSlots; // 30분 단위 시간 목록
}
