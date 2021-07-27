package org.example.unary.server;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UnaryProducerServiceImpl extends UnaryProducerServiceGrpc.UnaryProducerServiceImplBase {

    @Override
    public void call(DemoRequest request, StreamObserver<DemoResponse> responseObserver) {
        responseObserver.onNext(DemoResponse.newBuilder()
                .setMessage(request.getMessage() + " :: Processed")
                .setStatus(Status.STATUS_OK)
                .build());
        responseObserver.onCompleted();
    }
}
