package com.heshanthenura.fennec;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.heshanthenura.Victim;

public class Main {

    public static List<Victim> victims = new ArrayList<>();
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            try {
                acceptClients();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Server Started on port 8080");

        while (true) {
            System.out.print("--> ");
            int input = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            if (input == 100) {
                for (Victim victim : victims) {
                    System.out.println(victim.getId());
                }
            } else if (input >= 0 && input < victims.size()) {
                System.out.println("sent msg");
                victims.get(input).sendMessage("Message from server");
            } else {
                System.out.println("Invalid input. Please enter a valid victim index or 100 to list all.");
            }
        }
    }

    public static void acceptClients() throws IOException {
        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                victims.add(new Victim(clientSocket));
            }
        }
    }
}
