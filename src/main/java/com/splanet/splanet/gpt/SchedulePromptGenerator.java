package com.splanet.splanet.gpt;

import org.springframework.stereotype.Component;

@Component
public class SchedulePromptGenerator {

    public String generateSchedulePrompt(ScheduleRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("splanet은 사용자가 입력한 스케줄 정보에 맞춰 맞춤형 플래너를 제공하는 서비스입니다. 사용자가 음성으로 입력한 정보를 분석하여 최적의 스케줄을 제시해야 합니다.\n\n")
                .append("사용자가 다음과 같은 정보를 입력했습니다. 이 정보를 바탕으로 3개의 서로 다른 스케줄을 추천해 주세요. 각 스케줄은 고유한 컨셉을 가져야 하며, 하루 24시간을 30분 단위로 쪼개고, 각 업무의 시작 시간과 종료 시간을 포함해야 합니다.\n\n")
                .append("요청 정보:\n")
                .append("- 스케줄 기간: \"").append(request.getSchedulePeriod()).append("\"\n")
                .append("- 업무 목록: ").append(request.getTaskList()).append("\n")
                .append("- 업무 소요 시간: ").append(request.getTaskDurations()).append("\n")
                .append("- 우선순위: ").append(request.getPriority()).append("\n")
                .append("- 스케줄 컨셉: \"").append(request.getScheduleConcepts().get(0)).append("\", \"")
                .append(request.getScheduleConcepts().get(1)).append("\", \"")
                .append(request.getScheduleConcepts().get(2)).append("\"\n")  // 각 컨셉을 따로 넣도록
                .append("- 하루를 30분 단위로 쪼갠 시간 목록: ").append(request.getTimeSlots()).append("\n")
                .append("위 정보를 바탕으로 최적의 플래너 스케줄을 추천할 때 다음 사항을 준수해주세요:\n")
                .append("1. 요청된 모든 일정을 사용해야 합니다.\n")
                .append("2. 각 스케줄은 고유한 컨셉을 가져야 하며, 3개의 서로 다른 스케줄을 추천합니다.\n")
                .append("3. 추천 예시: 사용자가 선택할 수 있도록 3개의 추천 스케줄을 제공합니다.\n")
                .append("4. 응답 형식: 답변은 JSON 형식으로만 제공해야 하며, 다음과 같은 구조를 따라야 합니다:\n")
                .append("   { \"schedules\": [{ \"concept\": \"스케줄 컨셉\", \"schedule\": [{ \"date\": \"MM-DD\", \"tasks\": [{ \"task\": \"업무명\", \"duration\": \"소요시간\", \"priority\": \"우선순위\", \"startTime\": \"시작시간\", \"endTime\": \"종료시간\" }] }] }] }] }\n")
                .append("5. 형식 규칙:\n")
                .append("   - 날짜는 MM-DD 형식으로, 시간은 24시간(30분 단위) 형식이어야 합니다.\n")
                .append("   - 입력받은 업무를 지정된 일정 안에 모두 포함시켜야 합니다.\n")
                .append("   - 우선순위는 고유한 정수 값이어야 합니다.\n")
                .append("   - 업무 시간은 주어진 스케줄 기간 안에만 분할하여 채울 수 있습니다.\n")
                .append("   - 입력받은 날짜 각각의 일정을 구현해야 합니다.\n")
                .append("6. 제외할 내용: 일정 JSON 외에는 다른 내용이 포함되지 않아야 하며, 스케줄링과 관련 없는 질문에는 \"이와 관련된 질문에는 답변할 수 없습니다.\"라고 응답해야 합니다.\n");

        return prompt.toString();
    }
}