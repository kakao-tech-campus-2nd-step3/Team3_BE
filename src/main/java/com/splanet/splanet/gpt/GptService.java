package com.splanet.splanet.gpt;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GptService {

    private final OpenAiApi openAiApi;
    private final String prompt = "splanet은 사용자가 입력한 스케줄 정보에 맞춰 맞춤형 플래너를 제공하는 서비스입니다. 사용자가 음성으로 입력한 정보를 분석하여 최적의 스케줄을 제시해야 합니다. \n" +
            "사용자가 다음과 같은 정보를 입력했습니다. 이 정보를 바탕으로 3개의 서로 다른 스케줄을 추천해 주세요. 각 스케줄은 고유한 컨셉을 가져야 하며, 하루 24시간을 30분 단위로 쪼개고, 각 업무의 시작 시간과 종료 시간을 포함해야 합니다.\n" +
            "\n" +
            "요청 정보:\n" +
            "- 스케줄 기간: {schedulePeriod}\n" +
            "- 업무 목록: {taskList}\n" +
            "- 업무 소요 시간: {taskDurations}\n" +
            "- 우선순위: {priority}\n" +
            "- 스케줄 컨셉: {scheduleConcepts}\n" +
            "- 하루를 30분 단위로 쪼갠 시간 목록: {timeSlots}\n" +
            "\n" +
            "위 정보를 바탕으로 최적의 플래너 스케줄을 추천할 때 다음 사항을 준수해주세요:\n" +
            "\n" +
            "1. 요청된 모든 일정을 사용해야 합니다.\n" +
            "2. 3개의 스케줄을 추천합니다.\n" +
            "3. 추천 예시: 사용자가 선택할 수 있도록 3개의 추천 스케줄을 제공합니다.\n" +
            "4. 응답 형식: 답변은 JSON 형식으로만 제공해야 하며, 다음과 같은 구조를 따라야 합니다:\n" +
            "   {\n" +
            "       \"schedules\": [\n" +
            "           {\n" +
            "               \"concept\": \"스케줄 컨셉\",\n" +
            "               \"schedule\": [\n" +
            "                   {\n" +
            "                       \"date\": \"MM-DD\",\n" +
            "                       \"tasks\": [\n" +
            "                           {\n" +
            "                               \"task\": \"업무명\",\n" +
            "                               \"duration\": \"소요시간\",\n" +
            "                               \"priority\": \"우선순위\",\n" +
            "                               \"startTime\": \"시작시간\",\n" +
            "                               \"endTime\": \"종료시간\"\n" +
            "                           }\n" +
            "                       ]\n" +
            "                   }\n" +
            "               ]\n" +
            "           }\n" +
            "       ]\n" +
            "   }\n" +
            "\n" +
            "5. 형식 규칙:\n" +
            "   - 날짜는 MM-DD 형식으로, 시간은 24시간(30분 단위) 형식이어야 합니다.\n" +
            "   - 입력받은 업무를 지정된 일정 안에 모두 포함시켜야 합니다.\n" +
            "   - 우선순위는 고유한 정수 값이어야 합니다.\n" +
            "   - 업무 시간은 주어진 스케줄 기간 안에만 분할하여 채울 수 있습니다.\n" +
            "   - 입력받은 날짜 각각의 일정을 구현해야 합니다. 예를 들어, 5일 간의 스케줄을 제시해야 합니다.\n" +
            "6. 제외할 내용: 일정 JSON 외에는 다른 내용이 포함되지 않아야 하며, 스케줄링과 관련 없는 질문에는 \"이와 관련된 질문에는 답변할 수 없습니다.\"라고 응답해야 합니다.\n" +
            "\n" +
            "응답은 JSON 형식으로만 제공되어야 하며, 최적의 스케줄링 결과를 반영해야 합니다. 각 업무 내용, 소요 시간, 우선순위를 기준으로 업무를 적절한 시간대에 배치하여 최적의 스케줄을 생성해주세요.";

    public GptService(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    public String callGptApi(String call) {
        OpenAiApi.ChatCompletionMessage systemMessage = new OpenAiApi.ChatCompletionMessage(
                prompt, OpenAiApi.ChatCompletionMessage.Role.SYSTEM);
        OpenAiApi.ChatCompletionMessage userMessage = new OpenAiApi.ChatCompletionMessage(
                call, OpenAiApi.ChatCompletionMessage.Role.USER);

        // ChatCompletionRequest 생성 (기본 프롬프트와 사용자 메시지 포함)
        OpenAiApi.ChatCompletionRequest request = new OpenAiApi.ChatCompletionRequest(
                List.of(systemMessage, userMessage),  // 시스템 메시지 + 사용자 메시지 목록
                OpenAiApi.ChatModel.GPT_4_O_MINI.getValue(),  // 사용할 모델 (GPT-4o-mini)
                0.7  // 온도 설정 (응답의 창의성 정도)
        );

        // OpenAiApi를 통해 요청 보내고 응답 받기
        ResponseEntity<OpenAiApi.ChatCompletion> responseEntity = openAiApi.chatCompletionEntity(request);
        OpenAiApi.ChatCompletion completion = responseEntity.getBody();

        // 첫 번째 응답의 내용 반환
        if (completion != null && !completion.choices().isEmpty()) {
            return completion.choices().get(0).message().content();
        } else {
            return "응답을 받을 수 없습니다.";
        }
    }
}
