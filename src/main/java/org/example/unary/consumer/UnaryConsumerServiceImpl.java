package org.example.unary.consumer;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.unary.server.DemoRequest;
import org.example.unary.server.DemoResponse;
import org.example.unary.server.UnaryProducerServiceGrpc;

@GrpcService
public class UnaryConsumerServiceImpl extends UnaryConsumerServiceGrpc.UnaryConsumerServiceImplBase {

    @GrpcClient("unary")
    private UnaryProducerServiceGrpc.UnaryProducerServiceBlockingStub producerService;

    @Override
    public void call(Empty request, StreamObserver<FinalResponse> responseObserver) {
        responseObserver.onNext(convert(producerService.call(DemoRequest.newBuilder().setMessage("Example").build())));
        responseObserver.onCompleted();
    }

    private FinalResponse convert(DemoResponse demoResponse) {
        return FinalResponse.newBuilder()
                .setMessage(demoResponse.getMessage() + " || " + demoResponse.getStatus())
                .build();
    }
}
