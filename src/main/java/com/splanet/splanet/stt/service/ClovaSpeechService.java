package com.splanet.splanet.stt.service;

import com.google.protobuf.ByteString;
import com.nbp.cdncp.nest.grpc.proto.v1.NestResponse;
import io.grpc.stub.StreamObserver;

public interface ClovaSpeechService {
    StreamObserver<ByteString> recognize(StreamObserver<NestResponse> responseObserver);
}
