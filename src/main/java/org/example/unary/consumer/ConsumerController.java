package org.example.unary.consumer;

import lombok.Builder;
import lombok.Value;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.unary.server.DemoRequest;
import org.example.unary.server.DemoResponse;
import org.example.unary.server.UnaryProducerServiceGrpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @GrpcClient("unary")
    private UnaryProducerServiceGrpc.UnaryProducerServiceBlockingStub producerService;

    @GetMapping("/consumer")
    public FinalResponse consume() {
        return convert(producerService.call(DemoRequest.newBuilder().setMessage("Spring MVC").build()));
    }

    private FinalResponse convert(DemoResponse demoResponse) {
        return FinalResponse.builder()
                .message(demoResponse.getMessage() + " || " + demoResponse.getStatus())
                .build();
    }

    @Builder
    @Value
    private static class FinalResponse {
        String message;
    }
}
