package com.splanet.splanet.core.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.nbp.cdncp.nest.grpc.proto.v1.NestResponse;
import com.splanet.splanet.stt.service.ClovaSpeechGrpcService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SpeechWebSocketHandler extends BinaryWebSocketHandler {

    private final ClovaSpeechGrpcService clovaSpeechGrpcService;
    private final Map<String, StreamObserver<ByteString>> clientObservers = new ConcurrentHashMap<>();

    public SpeechWebSocketHandler(ClovaSpeechGrpcService clovaSpeechGrpcService) {
        this.clovaSpeechGrpcService = clovaSpeechGrpcService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 세션이 열릴 때마다 새로운 gRPC 스트림을 생성
        StreamObserver<NestResponse> responseObserver = new StreamObserver<NestResponse>() {
            @Override
            public void onNext(NestResponse value) {
                // 서버로부터 받은 응답 처리
                try {
                    String contents = value.getContents(); // JSON 문자열

                    // JSON 파싱
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(contents).getAsJsonObject();

                    if (jsonObject.has("transcription")) {
                        JsonObject transcription = jsonObject.getAsJsonObject("transcription");
                        String text = transcription.get("text").getAsString();
                        // 클라이언트로 text 필드만 전송
                        session.sendMessage(new TextMessage(text));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                try {
                    session.sendMessage(new TextMessage("오류 발생: " + t.getMessage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCompleted() {
                // 스트림 완료 처리
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // 오디오 데이터를 전송할 StreamObserver 생성
        StreamObserver<ByteString> requestObserver = clovaSpeechGrpcService.recognize(responseObserver);
        clientObservers.put(session.getId(), requestObserver);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // 클라이언트로부터 받은 오디오 데이터를 gRPC 서비스로 전달
        StreamObserver<ByteString> requestObserver = clientObservers.get(session.getId());
        if (requestObserver != null) {
            byte[] audioData = message.getPayload().array();
            ByteString audioChunk = ByteString.copyFrom(audioData);
            requestObserver.onNext(audioChunk);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션이 종료되면 gRPC 스트림도 종료
        StreamObserver<ByteString> requestObserver = clientObservers.remove(session.getId());
        if (requestObserver != null) {
            requestObserver.onCompleted();
        }
    }
}
