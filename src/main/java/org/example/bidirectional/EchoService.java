package org.example.bidirectional;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@GrpcService
public class EchoService extends EchoServiceGrpc.EchoServiceImplBase {
    private static final Logger LOGGER = Logger.getLogger(EchoService.class.getName());

    @Override
    public StreamObserver<EchoMessage> echo(StreamObserver<EchoMessage> responseObserver) {
        return new EchoObserver(responseObserver);
    }

    private static class EchoObserver implements StreamObserver<EchoMessage> {
        private final StreamObserver<EchoMessage> echoObserver;

        private EchoObserver(StreamObserver<EchoMessage> echoObserver) {
            this.echoObserver = echoObserver;
        }

        @Override
        public void onNext(EchoMessage echoMessage) {
            sleep();
            echoObserver.onNext(echoMessage);
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.log(Level.SEVERE, "Error during binary streaming rpc");
        }

        @Override
        public void onCompleted() {
            echoObserver.onCompleted();
        }

        private void sleep() {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Failed to sleep... don't drink this much coffee!");
            }
        }
    }
}
