package com.splanet.splanet.core.handler;

import com.splanet.splanet.stt.service.ClovaSpeechService;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpeechWebSocketHandler extends BinaryWebSocketHandler {

    private final ClovaSpeechService clovaSpeechService;
    private List<byte[]> audioDataBuffer = new ArrayList<>();
    private static final int MINIMUM_AUDIO_SIZE = 64000;  // 최소 데이터 크기를 96KB로 설정 (약 3초 분량)

    public SpeechWebSocketHandler(ClovaSpeechService clovaSpeechService) {
        this.clovaSpeechService = clovaSpeechService;
    }

    @Override
    protected synchronized void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        session.setBinaryMessageSizeLimit(256 * 1024);  // 메시지 크기 제한을 256KB로 설정
        byte[] audioData = message.getPayload().array();

        // 오디오 데이터를 버퍼에 추가
        audioDataBuffer.add(audioData);

        // 누적된 오디오 데이터 크기 계산
        int totalSize = audioDataBuffer.stream().mapToInt(arr -> arr.length).sum();

        // 현재 누적된 데이터 크기를 로그로 출력
        System.out.println("현재 누적된 데이터 크기: " + totalSize + " bytes");

        // 오디오 데이터가 충분히 쌓였을 때만 CLOVA API로 전송
        if (totalSize >= MINIMUM_AUDIO_SIZE) {
            byte[] fullAudioData = mergeAudioData();
            try {
                // CLOVA API로 전송
                String transcript = clovaSpeechService.recognize(fullAudioData);
                session.sendMessage(new TextMessage(transcript));
                // 인식에 성공했으므로 버퍼를 초기화
                audioDataBuffer.clear();
                System.out.println("인식 성공: 버퍼를 초기화합니다.");
            } catch (Exception e) {
                e.printStackTrace();
                // STT007 오류 발생 시 버퍼를 유지하고 데이터 수집 계속
                if (e.getMessage().contains("STT007")) {
                    System.err.println("오류 발생: STT007 - 데이터가 너무 작습니다. 더 많은 데이터를 수집 중...");
                    // 버퍼를 유지하여 다음 데이터를 기다립니다.
                } else {
                    // 다른 오류 발생 시 버퍼를 초기화하고 오류 메시지 전송
                    audioDataBuffer.clear();
                    session.sendMessage(new TextMessage("오류 발생: " + e.getMessage()));
                    System.err.println("오류 발생: " + e.getMessage() + " - 버퍼를 초기화합니다.");
                }
            }
        } else {
            // 아직 데이터가 충분하지 않으면 아무 작업도 하지 않음
            System.out.println("데이터가 아직 충분하지 않음");
        }
    }

    // 누적된 오디오 데이터를 병합하는 메서드
    private byte[] mergeAudioData() {
        int totalLength = audioDataBuffer.stream().mapToInt(arr -> arr.length).sum();
        byte[] mergedData = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] data : audioDataBuffer) {
            System.arraycopy(data, 0, mergedData, currentIndex, data.length);
            currentIndex += data.length;
        }
        return mergedData;
    }
}
