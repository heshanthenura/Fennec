package com.heshanthenura.fennec;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
public class WebSocketServer {

    private static final CopyOnWriteArraySet<Session> clients = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onConnect(Session session) {
        clients.add(session);
        System.out.println("New client connected: " + session.getId());
        sendToClient(session, "Welcome! Your session ID is: " + session.getId());
    }

    @OnClose
    public void onDisconnect(Session session) {
        clients.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received from client " + session.getId() + ": " + message);
        sendToClient(session, "Echo: " + message);
        broadcast("Client " + session.getId() + " says: " + message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void sendToClient(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (Session client : clients) {
            try {
                if (client.isOpen()) {
                    client.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
