package com.heshanthenura.fennec;

import org.glassfish.tyrus.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/", null, WebSocketServer.class);

        try {
            server.start();
            System.out.println("WebSocket server started on ws://localhost:8080/ws");
            System.in.read();  // Keep the server running
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
