package com.chat.app.view;

import com.chat.app.process.Client;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class createGroupController implements Initializable {
    @FXML
    private Button btCreate;
    @FXML
    private ListView<String> lvUser;

    @FXML
    private TextField tfGroupName;
    @FXML
    private ListView lvSelectedUser;

    private String groupName = "";
    private Stage cG;
    private Set<String> list = new HashSet<>();
    private Set<String> DefaultSelected = new HashSet<>();
    private boolean isCreate = true;

    public void setcG(Stage cG) {
        this.cG = cG;
    }


    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public void setCreate(boolean create) {
        isCreate = create;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DefaultSelected.add(Client.userName);
        ObservableList<String> chats = FXCollections.observableArrayList(Client.UserMessages.keySet());
        lvUser.setItems(chats);
        lvUser.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<String> DefaultObselectedUser = FXCollections.observableArrayList(DefaultSelected);
        lvSelectedUser.setItems(DefaultObselectedUser);
        this.list = DefaultSelected;
        lvUser.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    ObservableList<String> selectedItems = lvUser.getSelectionModel().getSelectedItems();
                    Set<String> list = new HashSet<>(DefaultSelected);
                    for (String name : selectedItems) {
                        list.add(name);
                    }
                    this.list = list;
                    ObservableList<String> ObselectedUser = FXCollections.observableArrayList(this.list);
                    lvSelectedUser.setItems(ObselectedUser);
                });

    }

    public void setInfo(String groupName) {
        isCreate = false;
        tfGroupName.setText(groupName);
        tfGroupName.setDisable(true);
        btCreate.setText("Add Member");

        this.DefaultSelected = new HashSet<>(Client.GroupMembers.get(groupName));
        ObservableList<String> selectedUser = FXCollections.observableArrayList(DefaultSelected);
        lvSelectedUser.setItems(selectedUser);

        Set<String> all = new HashSet<>(Client.UserMessages.keySet()) ;
        all.removeAll(selectedUser);
        ObservableList<String> Oball = FXCollections.observableArrayList(all);
        lvUser.setItems(Oball);
    }

    public void caGroup() throws IOException {
        Set<String> set = new HashSet<>(this.list);
        String selectedUsers = Client.userName + "|";
        String groupName = tfGroupName.getText();
        if (!Client.GroupMessages.containsKey(groupName)) {
            Client.GroupMessages.put(groupName, groupName + " has created!");
        }

        Client.GroupMembers.put(groupName, set);
        for (String user : set) {
            selectedUsers += user + "|";
        }
        String command = "*cagroup|" + groupName + "|" + selectedUsers;
        Client.sendCommandToServer(command);  // *newgroup|groupname|name1|name2|...|
        Client.home.refreshListView();
        this.cG.close();

    }


}

