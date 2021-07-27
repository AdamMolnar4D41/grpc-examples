# Unary call - Producer Side
## Concept
We are talking about an Unary RPC when the client makes a single request, and the server responds with a single respond.
It's similar to a normal function call.

## How to do it with Spring Boot gRPC?
Required: A proto file with a definition of a unary call. Also, we need the already built java files.
1. Create a java file, very similar to the "springless" grpc.
2. Implement the service using `onNext()` and `onCompleted()`
3. Add the `@GrpcServer` annotation to make it a bean and register our class to be started as a grpc server.
In the end you should end up with something like this:
```java
package org.example.unary.server;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Make it a bean and register it as a server
public class UnaryProducerServiceImpl extends UnaryProducerServiceGrpc.UnaryProducerServiceImplBase {

    @Override
    public void call(DemoRequest request, StreamObserver<DemoResponse> responseObserver) {
        responseObserver.onNext(DemoResponse.newBuilder()
                .setMessage(request.getMessage() + " :: Processed")
                .setStatus(Status.STATUS_OK)
                .build());
        responseObserver.onCompleted();
    }
}
```

## Start the server and make a call
1. Start the application as a regular Spring Boot project
2. Optional: Change the port of the application: `server.port=8080`
3. Optional: Change the port of the gRPC servers: `grpc.server.port=9090`
4. Make a call to the server using BloomRPC or similar tools

# Unary call - Consumer Side
## How to do it with Spring Boot gRPC?
Required: A java class that takes a request. It can be a rpc, or an HTTP request.
1. Create an implementation of the rpc service that we just wrote as a requirement.
2. Import the other server using `@GrpcClient("name-of-the-client")`
3. Call its endpoint with the required information: `producerService.call(DemoRequest.newBuilder().setMessage("Example").build())`
4. Use the response of the other service

```java
@GrpcService
public class UnaryConsumerServiceImpl extends UnaryClientServiceGrpc.UnaryClientServiceImplBase {

    @GrpcClient("unary")
    private UnaryProducerServiceGrpc.UnaryProducerServiceBlockingStub producerService;

    @Override
    public void call(Empty request, StreamObserver<FinalResponse> responseObserver) {
        responseObserver.onNext(convert(producerService.call(DemoRequest.newBuilder().setMessage("Example").build())));
        responseObserver.onCompleted();
    }

    private FinalResponse convert(DemoResponse demoResponse) {
        return FinalResponse.newBuilder()
                .setMessage(demoResponse.getMessage() + " || " + demoResponse.getStatus())
                .build();
    }
}
```

## Configure the client
```properties
grpc.client.unary.address=static://localhost:9090
grpc.client.unary.negotiation-type=plaintext
```

Congratulations, now you have two server communicating with each other using gRPC.
However, we used plaintext! This should only be used for development environments, use TLS/SSL for production!