package com.chat.app.view;

import com.chat.app.process.Client;
import com.chat.app.process.ReadThread;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class homeController implements Initializable {

    boolean isUserChat = true;
    @FXML
    private Label lbUser;
    @FXML
    private ListView lvUser;
    @FXML
    private ListView lvGroup;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lbReceive;
    @FXML
    private TextArea taMessages;
    @FXML
    private TextArea taSend;
    @FXML
    private Button btAddMember;
    @FXML
    private Button btQuitGroup;
    @FXML
    private ListView lvGroupMembers;
    @FXML
    private Button btSend;

    private ReadThread rthread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Client.home = this;
            Client.UserMessages = Client.getChats();
            lbUser.setText(Client.userName);
            refreshListView();

            lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String o, String t1) {
                    lvGroup.getSelectionModel().clearSelection();
                    isUserChat = true;
                    lbReceive.setText(t1);
                    refreshChatBox();
                    showGroupFeatures();
                }
            });

            lvGroup.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String o, String t1) {
                    lvUser.getSelectionModel().clearSelection();
                    isUserChat = false;
                    lbReceive.setText(t1);
                    refreshChatBox();
                    showGroupFeatures();
                    ObservableList<String> groupUsers = FXCollections.observableArrayList(Client.GroupMembers.get(t1));
                    lvGroupMembers.setItems(groupUsers);
                }
            });

            rthread = new ReadThread(Client.socket);
            rthread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGroupFeatures(){
        taSend.setDisable(false);
            btSend.setDisable(false);
            lvGroupMembers.setVisible(!isUserChat);
            btAddMember.setVisible(!isUserChat);
            btQuitGroup.setVisible(!isUserChat);


    }

    public void refreshChatBox(){
        String name = lbReceive.getText();
        String text = "";
        if(isUserChat){
            text =Client.UserMessages.get(name);
        }
        else{
            text =Client.GroupMessages.get(name);
        }
        taMessages.setText(text);
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String receiver = lbReceive.getText();
        String mess = Client.userName+":" + taSend.getText();

        taMessages.setText(taMessages.getText()+"\n"+mess);
        if(isUserChat){
        Client.sendMessage(mess, receiver);
        addMessage(mess, receiver);
        }
        else{
            Client.writer.println("*sendgroup|"+receiver+"|"+mess);
            String old = Client.GroupMessages.get(receiver);
            Client.GroupMessages.replace(receiver, old + "\n" + mess);
            Client.home.refreshChatBox();
        }
        taSend.setText("");
    }

    public void addMessage(String mess, String receiver){
        String oMess = Client.UserMessages.get(receiver);
        Client.UserMessages.replace(receiver, oMess+"\n"+mess);
    }

    public void addUser(String user) throws IOException {
        Client.UserMessages.put(user, "");
        refreshListView();
    }

    public void refreshListView() throws IOException {
        ObservableList<String> users = FXCollections.observableArrayList(Client.UserMessages.keySet());
        ObservableList<String> groups = FXCollections.observableArrayList(Client.GroupMessages.keySet());
        lvUser.setItems(users);
        lvGroup.setItems(groups);
    }

    public void createGroup(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation((getClass().getResource("createGroup.fxml")));
        AnchorPane root = (AnchorPane) loader.load();

        Stage createGroupStage = new Stage();

        createGroupStage.setScene(new Scene(root));
        createGroupStage.show();

        createGroupController controller = loader.getController();
        controller.setcG(createGroupStage);

    }

    public void addMemberToGroup(ActionEvent actionEvent) throws IOException {
//        Client.writer.println("*luig|"+lbReceive.getText());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation((getClass().getResource("createGroup.fxml")));
        AnchorPane root = (AnchorPane) loader.load();

        Stage createGroupStage = new Stage();

        createGroupStage.setScene(new Scene(root));
        createGroupStage.show();

        createGroupController controller = loader.getController();
        controller.setcG(createGroupStage);
        controller.setInfo(lbReceive.getText());
    }

    public void quitGroup(ActionEvent actionEvent) throws IOException {
        Client.writer.println("*quitgroup|"+lbReceive.getText());
        Client.GroupMessages.remove(lbReceive.getText());
        Client.home.refreshListView();
    }

    public void quit(ActionEvent actionEvent) throws IOException {
        Platform.exit();
    }

    public void showAddMemberGUI(String[] splited) throws IOException { // *luig|groupname|user1|user2|
        List<String> list = new ArrayList<>();

        for(int i = 2; i<splited.length; i++){
            list.add(splited[i]);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation((getClass().getResource("../view/createGroup.fxml")));
        AnchorPane root = (AnchorPane) loader.load();

        Stage createGroupStage = new Stage();

        createGroupStage.setScene(new Scene(root));
        createGroupStage.show();

        createGroupController controller = loader.getController();
        controller.setcG(createGroupStage);
        controller.setInfo(splited[1]);

    }
}
