package org.example.streaming.client;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext
public class MergerServiceImplTest {

    @GrpcClient("inProcess")
    private MergerServiceGrpc.MergerServiceStub mergerService;

    @Test
    @DirtiesContext
    public void testMergeShouldReturnMergeEachRequest() throws InterruptedException {
        // GIVEN
        MergeInput input = MergeInput.newBuilder()
                .setMessage("A")
                .build();
        final List<MergeOutput> outputList = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        // WHEN
        StreamObserver<MergeInput> mergeObserver = mergerService.merge(new StreamObserver<MergeOutput>() {
            @Override
            public void onNext(MergeOutput mergeOutput) {
                outputList.add(mergeOutput);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        mergeObserver.onNext(input);
        mergeObserver.onNext(input);
        mergeObserver.onNext(input);
        mergeObserver.onNext(input);
        mergeObserver.onCompleted();

        // THEN
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(4, outputList.get(0).getInputCount());
        assertEquals("AAAA", outputList.get(0).getMessage());
    }
}