package com.example.edgar.a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by edgar on 6/1/16.
 */
public class ArduinoThread implements Runnable {

    public static final String SERVER_IP = "192.168.0.1";
    public static final int SERVER_PORT = 23;

    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isRunning;
    private OnMessageReceived messageListener = null;
    private Socket socket;

    public ArduinoThread(OnMessageReceived listener) {
        this.messageListener = listener;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isRunning = true;
            while (isRunning) {
                String message = reader.readLine();
                this.messageListener.messageReceived(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (writer != null && !writer.checkError()) {
            writer.println(message);
            writer.flush();
        }
    }

    public void closeClient() {

        try {
            isRunning = false;
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
