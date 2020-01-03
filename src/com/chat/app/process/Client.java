package com.chat.app.process;

import com.chat.app.view.homeController;
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
    public static homeController home;

    public static void init() {
        try {
            socket = new Socket(hostName, hostPort);
            System.out.println("Connected to the chat server");
            OutputStream os = socket.getOutputStream();
            writer = new PrintWriter(os, true);

            InputStream is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));


        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public static void login(){
        writer.println(userName);
    }

    public static Map<String, String> getChats() throws IOException {
        writer.println(userName);
        String response = reader.readLine();
//        response = response.replaceFirst(".$","");
        String splited[] = response.split("\\|");

        Map<String, String> rs = new HashMap<>();
        for(String s: splited){
            if(!s.isEmpty())
                rs.put(s, "");
        }
        return rs;
    }

    public static void sendMessage(String mess,String receiver){
        writer.println(receiver+"|"+mess);
    }

}
