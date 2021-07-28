package org.example.streaming.server;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext
public class RepeaterServiceImplTest {

    @GrpcClient("inProcess")
    private RepeaterServiceGrpc.RepeaterServiceBlockingStub repeaterService;

    @Test
    @DirtiesContext
    public void testRepeatShouldReturnRepeatedResponse() {
        // GIVEN
        RepeatMessage repeatMessage = RepeatMessage.newBuilder()
                .setMessage("To be repeated")
                .build();

        // WHEN
        Iterator<RepeatMessage> messageIterator = repeaterService.repeat(repeatMessage);

        //THEN
        assertNotNull(messageIterator);
        messageIterator.forEachRemaining(answerMessage -> assertEquals(repeatMessage, answerMessage));
    }
}