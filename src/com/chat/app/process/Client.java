package com.chat.app.process;

import com.chat.app.view.homeController;
import com.chat.app.view.loginController;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client {
    public static String hostName = "localhost";
    public static int hostPort=6969;
    public static String userName;
    public static Socket socket;
    public static PrintWriter writer;
    public static BufferedReader reader;
    public static Stage loginStage;
    public static Map<String, String> UserMessages = new HashMap<String, String>();
    public static Map<String, String> GroupMessages = new HashMap<String, String>();
    public static Map<String, Set<String>> GroupMembers = new HashMap<>();
    public static loginController login;
    public static homeController home;


    public static void init() {
        try {
            socket = new Socket(hostName, hostPort);
            System.out.println("Connected to the chat server");
            OutputStream os = socket.getOutputStream();
            writer = new PrintWriter(os, true);

            ReadThread readThread = new ReadThread(socket);
            readThread.start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public static void sendCommandToServer(String command){
        writer.println(command);
    }

}
