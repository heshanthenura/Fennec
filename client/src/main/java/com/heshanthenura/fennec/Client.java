package com.heshanthenura.fennec;

import java.net.URI;
import java.util.Scanner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient {

    public Client(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to server!");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message from server: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        try {
            URI serverUri = new URI("ws://192.168.1.101:8080");
            Client client = new Client(serverUri);
            client.connect();


            while (!client.isOpen()) {

            }


            Scanner scanner = new Scanner(System.in);


            while (true) {
                System.out.print("Enter message to send (type 'exit' to quit): ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    client.close();
                    break;
                }


                client.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
