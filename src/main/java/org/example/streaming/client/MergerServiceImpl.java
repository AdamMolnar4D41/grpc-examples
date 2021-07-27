package org.example.streaming.client;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.logging.Level;
import java.util.logging.Logger;

@GrpcService
public class MergerServiceImpl extends MergerServiceGrpc.MergerServiceImplBase {
    private static final Logger LOGGER = Logger.getLogger(MergerServiceImpl.class.getName());

    @Override
    public StreamObserver<MergeInput> merge(StreamObserver<MergeOutput> responseObserver) {
        return new MergeObserver(responseObserver);
    }

    private static class MergeObserver implements StreamObserver<MergeInput> {
        private final StringBuilder builder = new StringBuilder();
        private final StreamObserver<MergeOutput> outputStreamObserver;
        private int inputCount = 0;

        public MergeObserver(StreamObserver<MergeOutput> outputStreamObserver) {
            this.outputStreamObserver = outputStreamObserver;
        }

        @Override
        public void onNext(MergeInput mergeInput) {
            builder.append(mergeInput.getMessage());
            inputCount++;
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.log(Level.SEVERE, "Error during client streaming rpc");
        }

        @Override
        public void onCompleted() {
            outputStreamObserver.onNext(MergeOutput.newBuilder()
                    .setMessage(builder.toString())
                    .setInputCount(inputCount)
                    .build());
            outputStreamObserver.onCompleted();
        }
    }
}
