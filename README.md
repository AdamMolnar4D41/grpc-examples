# gRPC with Spring Boot - Generic information
## Required maven dependencies and plugins:
### Spring Boot
1. Create a Spring Boot project
### Plugins:
1. `kr.motd.maven:os-maven-plugin` is required for the protobuf maven plugin
2. `org.xolstice.maven.plugins:protobuf-maven-plugin` adds a step to the maven lifecycle when java classes are generated from the proto files
3. `org.springframework.boot:spring-boot-maven-plugin`
### Dependencies:
1. `net.devh:grpc-spring-boot-starter:${spring.grpc.version}` Spring Boot support for grpc https://github.com/yidongnan/grpc-spring-boot-starter
This adds both the server and client.
   * Client only: `net.devh:grpc-client-spring-boot-starter`
   * Server only: `net.devh:grpc-server-spring-boot-starter`
3. `org.apache.tomcat:annotations-api:${annotations.api.version}` needed from java 9+

## Steps for project setup (only needed once per dev per project)
1. `mvn clean compile` to generate the proto file for the first time
2. Check the target directory for the generated files `target/generated-sources/protobuf`. Here you can see a `java` and a `grpc-java`folders
3. If they are already marked as generated source root then the initial setup is done, nothing else to do
4. If not then mark both as generated source root, the folder icon becomes blue with a fan icon.

## Proto file creation
1. Under the main directory create a new folder called `proto`, we will keep our proto files here. Feel free to organize them into additional libraries.
2. Mark it as a source folder. The icon should be blue at this point.
3. If you not have the `protobuf` plugin installed in your IntelliJ then install it :-)
4. Create your first proto file: `unary-server.proto`. If you have the above mentioned plugin installed than it will have a special icon.

```protobuf
syntax = "proto3"; // syntax selection, it can be proto2 or proto3.

package org.example.unary.server; // This to prevent name clashes between protocol message types.

option java_package = "org.example.unary.server"; // java package for our generated code
option java_multiple_files = true; // It'll generate multiple files and not one for all class

service UnaryProducerService { // our first service
    rpc call(DemoRequest) returns (DemoResponse);
}

message DemoRequest {
   string message = 1;
}

message DemoResponse {
    Status status = 1; // a custom object in the response, note: index must be a positive integer
    string message = 2; // a string in the response
}

enum Status {
    STATUS_OK = 0; // unlike a message, here we should start with zero index
    STATUS_UNSPECIFIED = 1;
    STATUS_ERROR = 2;
}
```
5. Run a `mvn clean compile` to generate the java code

Congratulations, you now have a proto file, and some generated classes ready to be used. 
Read the other readme files to learn how to create a server and a client.