package org.example.order.kitchen;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.order.manager.OrderMessage;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@GrpcService
public class KitchenService extends KitchenServiceGrpc.KitchenServiceImplBase {
    private static final Logger LOGGER = Logger.getLogger(KitchenService.class.getName());

    @Override
    public StreamObserver<OrderMessage> prepareFood(StreamObserver<FinishedProduct> responseObserver) {
        return new FoodObserver(responseObserver);
    }

    private static class FoodObserver implements StreamObserver<OrderMessage> {
        private final StreamObserver<FinishedProduct> productObserver;

        private FoodObserver(StreamObserver<FinishedProduct> productObserver) {
            this.productObserver = productObserver;
        }

        @Override
        public void onNext(OrderMessage orderMessage) {
            for (int i = 0; i < orderMessage.getAmount(); i++) {
                sleep();
                if (i == 3) { // ;)
                    productObserver.onCompleted();
                }
                productObserver.onNext(FinishedProduct.newBuilder()
                        .setProduct(orderMessage.getProduct())
                        .build());
            }
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.log(Level.SEVERE, "Error during binary streaming rpc");
        }

        @Override
        public void onCompleted() {
            productObserver.onCompleted();
        }

        private void sleep() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Failed to sleep... don't drink this much coffee!");
            }
        }
    }
}
