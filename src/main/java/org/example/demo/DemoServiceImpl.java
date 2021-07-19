package org.example.demo;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

public class DemoServiceImpl extends DemoServiceGrpc.DemoServiceImplBase {

    @Override
    public void call(Empty request, StreamObserver<DemoResponse> responseObserver) {
        responseObserver.onNext(createResponse()); // Publish some data to the stream. Never call it after onError or onCompleted
        responseObserver.onCompleted(); // Marks the end of stream, should be the last thing.
    }

    private DemoResponse createResponse() {
        return DemoResponse.newBuilder()
            .setStatus(Status.STATUS_OK)
            .setDescription("Some string value")
            .build();
    }
}
