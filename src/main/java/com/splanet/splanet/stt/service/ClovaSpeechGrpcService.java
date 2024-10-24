package com.splanet.splanet.stt.service;

import com.google.protobuf.ByteString;
import com.nbp.cdncp.nest.grpc.proto.v1.*;
import com.splanet.splanet.core.properties.ClovaProperties;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class ClovaSpeechGrpcService implements ClovaSpeechService {

    private final NestServiceGrpc.NestServiceStub nestServiceStub;
    private final ClovaProperties clovaProperties;

    public ClovaSpeechGrpcService(ClovaProperties clovaProperties) {
        this.clovaProperties = clovaProperties;

        // gRPC 채널 생성
        ManagedChannel channel = NettyChannelBuilder
                .forAddress("clovaspeech-gw.ncloud.com", 50051)
                .useTransportSecurity()
                .build();

        // Stub 생성 및 인증 정보 설정
        NestServiceGrpc.NestServiceStub stub = NestServiceGrpc.newStub(channel);
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + clovaProperties.getClientSecret());
        this.nestServiceStub = MetadataUtils.attachHeaders(stub, metadata);
    }

    @Override
    public StreamObserver<ByteString> recognize(StreamObserver<NestResponse> responseObserver) {
        StreamObserver<NestRequest> requestObserver = nestServiceStub.recognize(responseObserver);

        // Config 메시지 전송
        requestObserver.onNext(createConfigRequest(clovaProperties.getLanguage()));

        return new StreamObserver<ByteString>() {
            private int sequenceId = 0;

            @Override
            public void onNext(ByteString audioChunk) {
                NestRequest dataRequest = createDataRequest(audioChunk, sequenceId, false);
                requestObserver.onNext(dataRequest);
                sequenceId++;
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                requestObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                requestObserver.onCompleted();
            }
        };
    }

    // Config 설정
    private NestRequest createConfigRequest(String language) {
        NestConfig config = NestConfig.newBuilder()
                .setConfig("{\"transcription\":{\"language\":\"" + language + "\"}}")
                .build();

        return NestRequest.newBuilder()
                .setType(RequestType.CONFIG)
                .setConfig(config)
                .build();
    }

    // 데이터 구성
    private NestRequest createDataRequest(ByteString audioChunk, int sequenceId, boolean epFlag) {
        NestData data = NestData.newBuilder()
                .setChunk(audioChunk)
                .setExtraContents("{\"seqId\":" + sequenceId + ",\"epFlag\":" + epFlag + "}")
                .build();

        return NestRequest.newBuilder()
                .setType(RequestType.DATA)
                .setData(data)
                .build();
    }
}
