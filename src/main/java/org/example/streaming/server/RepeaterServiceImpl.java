package org.example.streaming.server;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@GrpcService
public class RepeaterServiceImpl extends RepeaterServiceGrpc.RepeaterServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(RepeaterServiceImpl.class.getName());

    @Override
    public void repeat(RepeatMessage request, StreamObserver<RepeatMessage> responseObserver) {
        sleep();
        responseObserver.onNext(request);
        sleep();
        responseObserver.onNext(request);
        sleep();
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to sleep... don't drink this much coffee!");
        }
    }
}
