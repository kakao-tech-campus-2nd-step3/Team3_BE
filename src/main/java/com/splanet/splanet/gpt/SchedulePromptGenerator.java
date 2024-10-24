package com.splanet.splanet.gpt;

import org.springframework.stereotype.Component;

@Component
public class SchedulePromptGenerator {

    public String generateSchedulePrompt(ScheduleRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("splanet은 사용자가 입력한 스케줄 정보를 바탕으로 맞춤형 플래너를 제공하는 서비스입니다. 사용자가 음성으로 입력한 정보를 분석하여 최적의 스케줄을 제시해야 합니다.\n\n")
                .append("사용자가 다음과 같은 정보를 입력했습니다. 이 정보를 바탕으로 요청된 컨셉에 따라 스케줄을 추천해 주세요. 각 스케줄은 하루 24시간을 30분 단위로 쪼개고, 각 업무의 시작 시간과 종료 시간을 포함해야 합니다.\n\n")
                .append("요청 정보:\n")
                .append("- 스케줄 기간: \"10월 1일부터 10월 2일까지\"\n")
                .append("- 업무 목록: ").append(request.getTaskList()).append("\n")
                .append("- 업무 소요 시간: ").append(request.getTaskDurations()).append("\n")
                .append("- 우선순위: ").append(request.getPriority()).append("\n")
                .append("- 스케줄 컨셉: \"널널한 스케줄\", \"빡빡한 스케줄\"\n")
                .append("- 하루를 30분 단위로 쪼갠 시간 목록: ").append(request.getTimeSlots()).append("\n")
                .append("위 정보를 바탕으로 다음과 같은 조건을 준수하여 스케줄을 추천해주세요:\n")
                .append("1. 요청된 모든 일정을 사용해야 합니다.\n")
                .append("2. 각 스케줄은 요청된 두 개의 서로 다른 컨셉(사용자가 입력한다. 예시로는 널널한 스케줄, 빡빡한 스케줄이 있다.)을 따릅니다.\n")
                .append("3. 추천 예시: 사용자가 선택할 수 있도록 두 개의 추천 스케줄을 제공합니다.\n")
                .append("4. 응답 형식: 답변은 JSON 형식으로만 제공해야 하며, 다음과 같은 구조를 따라야 합니다:\n")
                .append("   { \"schedules\": [{ \"concept\": \"스케줄 컨셉\", \"schedule\": [{ \"date\": \"MM-DD\", \"tasks\": [{ \"task\": \"업무명\", \"duration\": \"소요시간\", \"priority\": \"우선순위\", \"startTime\": \"시작시간\", \"endTime\": \"종료시간\" }] }] }] }] }\n")
                .append("5. 형식 규칙:\n")
                .append("   - 날짜는 MM-DD 형식으로, 시간은 24시간(30분 단위) 형식이어야 합니다.\n")
                .append("   - 입력받은 업무를 지정된 일정 안에 모두 포함시켜야 합니다.\n")
                .append("   - 우선순위는 고유한 정수 값이어야 합니다.\n")
                .append("   - 업무 시간은 주어진 스케줄 기간 안에만 분할하여 채울 수 있습니다.\n")
                .append("   - 입력받은 날짜 각각의 일정을 구현해야 합니다.\n")
                .append("6. 예시: 결과값으로 각 날짜마다 널널한 스케줄과 빡빡한 스케줄 컨셉을 입력받으면, 스케줄1(빡빡한 스케줄): 10월1일+10월2일, 스케줄2(널널한 스케줄): 10월1일+10월2일 총 4개의 스케줄을 제시해야 합니다. 오직 스케줄 데이터 정보만 json으로 출력한다.\n")
                .append("7. 제외할 내용: 일정 JSON 외에는 다른 내용이 포함되지 않아야 하며, 스케줄링과 관련 없는 질문에는 \"이와 관련된 질문에는 답변할 수 없습니다.\"라고 응답해야 합니다.\n");

        return prompt.toString();
    }
}