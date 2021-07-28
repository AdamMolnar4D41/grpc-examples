package org.example.unary.server;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext // ensure that the server stops
public class UnaryProducerServiceImplTest {

    @GrpcClient("inProcess")
    private UnaryProducerServiceGrpc.UnaryProducerServiceBlockingStub producerService;

    @Test
    @DirtiesContext
    public void testCallShouldReturnProcessedResponse() {
        // GIVEN
        DemoRequest demoRequest = DemoRequest.newBuilder()
                .setMessage("Test message")
                .build();

        // WHEN
        DemoResponse demoResponse = producerService.call(demoRequest);

        // THEN
        assertNotNull(demoResponse);
        assertEquals("Test message :: Processed", demoResponse.getMessage());
    }
}