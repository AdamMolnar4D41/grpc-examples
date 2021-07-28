package org.example.bidirectional;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext
public class EchoServiceTest {

    @GrpcClient("inProcess")
    private EchoServiceGrpc.EchoServiceStub echoService;

    @Test
    @DirtiesContext
    public void testEchoShouldReturnEchoMessage() throws InterruptedException {
        EchoMessage message = EchoMessage.newBuilder()
                .setMessage("Send me back")
                .build();
        List<EchoMessage> messages = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<EchoMessage> echoObserver = echoService.echo(new StreamObserver<EchoMessage>() {
            @Override
            public void onNext(EchoMessage echoMessage) {
                messages.add(echoMessage);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        echoObserver.onNext(message);
        echoObserver.onNext(message);
        echoObserver.onNext(message);
        echoObserver.onCompleted();

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        assertEquals(List.of(message, message, message), messages);
    }
}