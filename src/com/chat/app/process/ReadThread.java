package com.chat.app.process;



import com.chat.app.view.createGroupController;
import com.chat.app.view.homeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

                if (response.startsWith("*luig")){
                    Client.home.showAddMemberGUI(splited);
                }

                if (response.startsWith("*userquit")){ // *userquit|username
                    Client.UserMessages.remove(splited[1]);

                    final String quittedUser = splited[1];

                    Client.GroupMembers.keySet().forEach((String groupName) -> {
                        if (Client.GroupMembers.get(groupName).contains(quittedUser)){

                            String old = Client.GroupMessages.get(groupName);
                            Client.GroupMessages.replace(groupName, old + "\n" + quittedUser + " has quit!");
                            Client.GroupMembers.get(groupName).remove(quittedUser);
                        }

                    });

                    Client.home.refreshChatBox();
                    Client.home.refreshListView();
                    Client.home.refreshListUserGroup();
                    continue;
                }

                if(response.startsWith("*quitgroup")){
                    Client.GroupMembers.get(splited[1]).remove(splited[2]);
                    Client.home.refreshListUserGroup();
                }

                if(response.startsWith("*newuser")){
                    String newUser = response.substring(8);
                    Client.home.addUser(newUser);
                    continue;
                }

                if (response.startsWith("*newgroup")){
                    final String groupName = splited[1];
                    Set<String> members = new HashSet<>();
                    for(int i = 2; i<splited.length; i++){
                        members.add(splited[i]);
                    }
                    System.out.println(members);
                    if (!Client.GroupMessages.containsKey(groupName)){
                        Client.GroupMessages.put(groupName, groupName + " has created!");
                    }
                    else{
                        members.forEach((String username) ->{
                            if(!Client.GroupMembers.get(groupName).contains(username)){
                                String old = Client.GroupMessages.get(groupName);
                                Client.GroupMessages.replace(groupName, old + "\n" + username + " has joined!");
                            }
                        });
                    }
                    Client.GroupMembers.put(groupName, members);
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

