package com.chat.app.process;



import com.chat.app.view.homeController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;


    public ReadThread(Socket s) {
        this.socket = s;
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        String splited[];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String response = reader.readLine();
                if(response==null) break;

                splited = response.split("\\|");
                System.out.println("Received: " + response);


                if (response.startsWith("*userquit")){
                    Client.UserMessages.remove(splited[1]);
                    Client.home.refreshListView();
                    continue;
                }

                if(response.startsWith("*newuser")){
                    String newUser = response.substring(8);
                    Client.home.addUser(newUser);
                    continue;
                }

                if (response.startsWith("*newgroup")){
                    Client.GroupMessages.put(splited[1], "");
                    Client.home.refreshListView();
                    continue;
                }

                if (response.startsWith("*sendgroup")){
                    if(splited.length<3) continue;
                    String old = Client.GroupMessages.get(splited[1]);
                    Client.GroupMessages.replace(splited[1], old + "\n" + splited[2]);
                    Client.home.refreshChatBox();
                    continue;
                }

                Client.home.addMessage( splited[1], splited[0]);
                Client.home.refreshChatBox();
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }

}

