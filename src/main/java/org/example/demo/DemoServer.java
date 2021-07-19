package org.example.demo;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class DemoServer {
    private Server server;
    private static final Logger LOGGER = Logger.getLogger(DemoServer.class.getName());

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
            LOGGER.log(Level.SEVERE, "Server start failed on port 8080");
        }
    }

    public void shutdownServerHook() {
        if (Objects.nonNull(server)) {                                // We should only shutdown the server if it's exists
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.awaitTermination(60, TimeUnit.SECONDS);       // Stops our server after 2 minutes
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Server stop interrupted");
                }
            }));
        }
    }
}
