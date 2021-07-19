# gRPC without Spring
## Required maven dependencies and plugins:
### Plugins:
1. `kr.motd.maven:os-maven-plugin` is required for the protobuf maven plugin
2. `org.xolstice.maven.plugins:protobuf-maven-plugin` adds a step to the maven lifecycle when java classes are generated from the proto files

### Dependencies:
1. `io.grpc:grpc-netty-shaded:${grpc.version}`
2. `io.grpc:grpc-protobuf:${grpc.version}`
3. `io.grpc:grpc-stub:${grpc.version}`
4. `org.apache.tomcat:annotations-api:${annotations.api.version}` needed from java 9+
5. Optional: `com.google.protobuf:protobuf-java-util:${protobuf.google.version}` adds some predefined goodies, for example Empty message

## Steps for project setup (only needed once per dev per project)
1. `mvn clean compile` to generate the proto file for the first time
2. Check the target directory for the generated files `target/generated-sources/protobuf`. Here you can see a `java` and a `grpc-java`folders
3. If they are already marked as generated source root then the initial setup is done, nothing else to do
4. If not then mark both as generated source root, the folder icon becomes blue with a fan icon.

## Proto file creation
1. Under the main directory create a new folder called `proto`, we will keep our proto files here. Feel free to organize them into additional libraries.
2. Mark it as a source folder. The icon should be blue at this point.
3. If you not have the `protobuf` plugin installed in your IntelliJ then install it :-)
4. Create your first proto file: `first.proto`. If you have the above mentioned plugin installed than it will have a special icon.

```protobuf
syntax = "proto3"; // syntax selection, it can be proto2 or proto3.

package org.example.demo; // This to prevent name clashes between protocol message types.

import "google/protobuf/empty.proto"; // import of an "Empty" message to be used in our messages. Needs the "com.google.protobuf:protobuf-java-util" dependency

option java_package = "org.example.demo"; // java package for our generated code
option java_multiple_files = true; // It'll generate multiple files and not one for all class

service DemoService { // our first service
    rpc call(google.protobuf.Empty) returns (DemoResponse); // for an empty call we should response with a DemoResponse message
}

message DemoResponse {
    Status status = 1; // a custom object in the response, note: index must be a positive integer
    string description = 2; // a string in the response
}

enum Status {
    STATUS_OK = 0; // unlike a message, here we should start with zero index
    STATUS_UNSPECIFIED = 1;
    STATUS_ERROR = 2;
}
```
5. Run a `mvn clean compile` to generate the java code

## Let's implement our service
1. The usual naming pattern is DemoServiceImpl. Starting with the name of the service from our proto file and adding Impl.
2. `public class DemoServiceImpl extends DemoServiceGrpc.DemoServiceImplBase` where `DemoServiceGrpc.DemoServiceImplBase` are both generated classes.
   If the generated classes are not shown by IntelliJ, please make sure that the generated source folder are marked as generated source folders.
3. Implement the abstract rpc method `call` in our example.
```java
package org.example.demo;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

public class DemoServiceImpl extends DemoServiceGrpc.DemoServiceImplBase {
    
    @Override
    public void call(Empty request, StreamObserver<DemoResponse> responseObserver) {
        responseObserver.onNext(createResponse()); // Publish some data to the stream. Never call it after onError or onCompleted
        responseObserver.onCompleted(); // Marks the end of stream, should be the last thing.
    }
    
    private DemoResponse createResponse() {
        return DemoResponse.newBuilder()
            .setStatus(Status.STATUS_OK)
            .setDescription("Some string value")
            .build();
    }
}
```

## Let's create our server
If we are done with our proto file, we generated our java code and we made the implementation of our logic than we should create a server to run our code.
`io.grpc.Server` will help us to do that.
1. Create a java class to have our server related code, name it `DemoServer` for this example.
2. Using the `io.grpc.ServerBuilder` create an instance of a server and start it. You can specify a port and a service to handle the requests.
3. Using the server we can create a method to stop the server. In the end we should end up with something like this:

```java
package org.example.demo;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DemoServer {
   private Server server;

   public static void main(String[] args) { // main method to start our server :-)
      DemoServer server = new DemoServer(); // because of the static context
      server.startServer();
      server.shutdownServerHook();
   }

   public void startServer() {
      try {
         server = ServerBuilder.forPort(8080)        // I decided to start the server on 8080, but it's your choice
                 .addService(new DemoServiceImpl())  // This it he service we created not long ago
                 .build()
                 .start();                           // You don't have to start this immediately, but in this case there is no reason to wait :-)
      } catch (IOException e) {
          // Your choice how to handle this exception
      }
   }

   public void shutdownServerHook() {
      if (Objects.nonNull(server)) {                                // We should only shutdown the server if it's exists
         Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
               server.awaitTermination(10, TimeUnit.SECONDS);       // Stops our server after 2 minutes
            } catch (InterruptedException e) {
               // Your choice how to handle this exception
            }
         }));
      }
   }
}
```

Congratulation you have your first gRPC server running! But how to test it? For REST we have Postman for example, but rpc is a little different than REST.
For example the communication not happens in JSON, so not human-readable format. We need something that converts the rpc response into a json for example.
A good tool to use is "BloomRPC" there are multiple tutorials and documentations on the internet, so I won't write it down.