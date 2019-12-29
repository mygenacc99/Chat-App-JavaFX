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
import java.util.ResourceBundle;

public class createGroupController implements Initializable {

    public void setcG(Stage cG) {
        this.cG = cG;
    }

    String groupName = "";

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    private Stage cG;
    private boolean isCreate = true;

    public void setCreate(boolean create) {
        isCreate = create;
    }

    String selectedUsers = Client.userName + "|";

    @FXML
    private Button btCreate;
    @FXML
    private ListView<String> lvUser;

    @FXML
    private TextField tfGroupName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> chats = FXCollections.observableArrayList(Client.UserMessages.keySet());
        lvUser.setItems(chats);
        lvUser.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        lvUser.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    ObservableList<String> selectedItems = lvUser.getSelectionModel().getSelectedItems();
                    selectedUsers = Client.userName + "|";
                    for (String name : selectedItems) {
                        selectedUsers += name + "|";
                    }

                });


    }

    public void setInfo(String groupName) {
        tfGroupName.setText(groupName);
        tfGroupName.setDisable(true);
        btCreate.setText("Add Member");
    }

    public void addGroup() throws IOException {

        String groupName = tfGroupName.getText();
        Client.GroupMessages.put(groupName, "");
        Client.writer.println("*newgroup|" + groupName + "|" + selectedUsers);  // *newgroup|groupname|name1|name2|...|
        Client.home.refreshListView();
        this.cG.close();

    }


}

