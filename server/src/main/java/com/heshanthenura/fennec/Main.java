package com.heshanthenura.fennec;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Started on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected" + clientSocket.getRemoteSocketAddress());
                }
            }

        }

    }
}