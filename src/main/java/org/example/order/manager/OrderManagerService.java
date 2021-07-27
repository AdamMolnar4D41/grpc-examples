package org.example.order.manager;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.order.kitchen.FinishedProduct;
import org.example.order.kitchen.KitchenServiceGrpc;

@GrpcService
public class OrderManagerService extends OrderManagerServiceGrpc.OrderManagerServiceImplBase {

    @GrpcClient("kitchen")
    private KitchenServiceGrpc.KitchenServiceStub kitchenService;

    @Override
    public StreamObserver<OrderMessage> placeOrder(StreamObserver<ProductMessage> responseObserver) {
        ProductMessage.Builder productMessageBuilder = ProductMessage.newBuilder();
        StreamObserver<OrderMessage> kitchenObserver = kitchenService.prepareFood(new StreamObserver<FinishedProduct>() {
            @Override
            public void onNext(FinishedProduct finishedProduct) {
                productMessageBuilder.addProducts(finishedProduct.getProduct()); // 2. after kitchen returns finished product
            }

            @Override
            public void onError(Throwable throwable) { }

            @Override
            public void onCompleted() {
                responseObserver.onNext(productMessageBuilder.build()); // 3. finish kitchen post work
                responseObserver.onCompleted();
            }
        });
        return new OrderObserver(kitchenObserver);
    }

    private static class OrderObserver implements StreamObserver<OrderMessage> {
        private final StreamObserver<OrderMessage> kitchenObserver;

        private OrderObserver(StreamObserver<OrderMessage> kitchenObserver) {
            this.kitchenObserver = kitchenObserver;
        }

        @Override
        public void onNext(OrderMessage orderMessage) {
            kitchenObserver.onNext(orderMessage); // 1. call kitchen service
        }

        @Override
        public void onError(Throwable throwable) { }

        @Override
        public void onCompleted() {
            kitchenObserver.onCompleted(); // mark end of kitchen calls
        }
    }
}
