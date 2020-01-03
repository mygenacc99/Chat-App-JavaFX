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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class homeController implements Initializable {

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
    boolean isUserChat = true;
    private String receiverName = "";
    String command = "";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Client.home = this;
            lbUser.setText(Client.userName);
            refreshListView();

            lvUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String o, String t1) {
                    lvGroup.getSelectionModel().clearSelection();
                    isUserChat = true;
                    lbReceive.setText(t1);
                    receiverName = t1;
                    refreshChatBox();
                    showGroupFeatures(true);
                }
            });

            lvGroup.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String o, String t1) {
                    lvUser.getSelectionModel().clearSelection();
                    isUserChat = false;
                    lbReceive.setText(t1);
                    receiverName = t1;
                    refreshChatBox();
                    refreshListUserGroup();
                    showGroupFeatures(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGroupFeatures(boolean show) {
        taSend.setDisable(!show);
        btSend.setDisable(!show);
        lvGroupMembers.setVisible(!isUserChat);
        btAddMember.setVisible(!isUserChat);
        btQuitGroup.setVisible(!isUserChat);
    }

    public void refreshChatBox() {
        String text = "";
        if (isUserChat) {
            text = Client.UserMessages.get(receiverName);
        } else {
            text = Client.GroupMessages.get(receiverName);
        }
        taMessages.setText(text);
    }

    public void refreshListUserGroup() {
        if (!isUserChat) {
            ObservableList<String> group;
            if(Client.GroupMembers.containsKey(receiverName))
                group = FXCollections.observableArrayList(Client.GroupMembers.get(receiverName));
            else{
                group = FXCollections.observableArrayList(new HashSet<>());
            }
            lvGroupMembers.setItems(group);
        }
    }

    public void refreshListView() throws IOException {
        ObservableList<String> users = FXCollections.observableArrayList(Client.UserMessages.keySet());
        ObservableList<String> groups = FXCollections.observableArrayList(Client.GroupMessages.keySet());
        lvUser.setItems(users);
        lvGroup.setItems(groups);
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String mess = Client.userName + ":" + taSend.getText();

        taMessages.setText(taMessages.getText() + "\n" + mess);
        if (isUserChat) {
            command = "*sendUser" + "|" + receiverName +"|" + mess;
            Client.sendCommandToServer(command);
            addMessage(mess, receiverName);
        } else {
            command = "*sendgroup|" + receiverName + "|" + mess;
            Client.sendCommandToServer(command);
            String old = Client.GroupMessages.get(receiverName);
            Client.GroupMessages.replace(receiverName, old + "\n" + mess);
            Client.home.refreshChatBox();
        }
        taSend.setText("");
    }

    public void addMessage(String mess, String receiver) {
        String oMess = Client.UserMessages.get(receiver);
        Client.UserMessages.replace(receiver, oMess + "\n" + mess);
    }

    public void addUser(String user) throws IOException {
        Client.UserMessages.put(user, "");
        refreshListView();
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
        command = "*quitgroup|" + receiverName;
        Client.sendCommandToServer(command);
        Client.GroupMessages.remove(receiverName);
        Client.home.refreshListView();
        isUserChat = true;
        showGroupFeatures(false);
    }

    public void quit(ActionEvent actionEvent) throws IOException {
        Platform.exit();
    }
}
