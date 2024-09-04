package com.heshanthenura;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Victim {
    private String id;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public Victim(Socket socket) throws IOException {
        this.socket = socket;
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.id = socket.getRemoteSocketAddress().toString();
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public String receiveMessage() throws IOException {
        return bufferedReader.readLine();
    }

    public void close() throws IOException {
        bufferedReader.close();
        printWriter.close();
        socket.close();
    }

    public String getId() {
        return id;
    }

}
