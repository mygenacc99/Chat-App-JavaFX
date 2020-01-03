package com.chat.app.process;



import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashSet;
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
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String response = reader.readLine();
                if (response == null) break;
                System.out.println(response);
                splited = response.split("\\|");

                if (response.startsWith("*loginfail")) {
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            Client.login.loginFail();
                        }
                    };
                    Platform.runLater(updater);
                    break;
                }
                if(response.startsWith("*listUser")){
                    for(int i = 1; i<splited.length; i++){
                        if(!splited[i].isEmpty()) Client.UserMessages.put(splited[i], "");
                    }
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Client.login.loginSuccess();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Platform.runLater(updater);
                    continue;
                }

                if (response.startsWith("*userquit")) { // *userquit|username
                    Client.UserMessages.remove(splited[1]);

                    final String quittedUser = splited[1];

                    Client.GroupMembers.keySet().forEach((String groupName) -> {
                        if (Client.GroupMembers.get(groupName).contains(quittedUser)) {

                            String old = Client.GroupMessages.get(groupName);
                            Client.GroupMessages.replace(groupName, old + "\n" + quittedUser + " has quit!");
                            Client.GroupMembers.get(groupName).remove(quittedUser);
                        }

                    });
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Client.home.refreshListUserGroup();
                                Client.home.refreshChatBox();
                                Client.home.refreshListView();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Platform.runLater(updater);

                    continue;
                }

                if (response.startsWith("*quitgroup")) {
                    Client.GroupMembers.get(splited[1]).remove(splited[2]);
                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            Client.home.refreshListUserGroup();
                        }
                    };
                    Platform.runLater(updater);
                    continue;
                }

                if (response.startsWith("*newuser")) {
                    String newUser = response.substring(8);

                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Client.home.addUser(newUser);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Platform.runLater(updater);

                    continue;
                }

                if (response.startsWith("*newgroup")) {
                    final String groupName = splited[1];
                    Set<String> members = new HashSet<>();
                    for (int i = 2; i < splited.length; i++) {
                        members.add(splited[i]);
                    }
                    System.out.println(members);
                    if (!Client.GroupMessages.containsKey(groupName)) {
                        Client.GroupMessages.put(groupName, groupName + " has created!");
                    } else {
                        members.forEach((String username) -> {
                            if (!Client.GroupMembers.get(groupName).contains(username)) {
                                String old = Client.GroupMessages.get(groupName);
                                Client.GroupMessages.replace(groupName, old + "\n" + username + " has joined!");
                            }
                        });
                    }
                    Client.GroupMembers.put(groupName, members);

                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Client.home.refreshListView();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Platform.runLater(updater);

                    continue;
                }

                if (response.startsWith("*sendgroup")) {
                    if (splited.length < 3) continue;
                    String old = Client.GroupMessages.get(splited[1]);
                    Client.GroupMessages.replace(splited[1], old + "\n" + splited[2]);

                    Runnable updater = new Runnable() {
                        @Override
                        public void run() {
                            Client.home.refreshChatBox();
                        }
                    };
                    Platform.runLater(updater);
                    continue;
                }

                Client.home.addMessage(splited[1], splited[0]);
                Runnable updater = new Runnable() {
                    @Override
                    public void run() {
                        Client.home.refreshChatBox();
                    }
                };
                Platform.runLater(updater);
            }
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error reading from server: " + ex.getMessage());
            ex.printStackTrace();
        }

    }


}

