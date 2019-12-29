package com.chat.app.view;


import com.chat.app.process.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {



    @FXML
    private TextField tfName;
    @FXML
    private TextField tfPass;

    public void loginButtonClick(ActionEvent actionEvent) throws IOException {
        String username = tfName.getText();
        Client.userName = username;
        Stage chatStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));

        chatStage.setScene(new Scene(root));
        chatStage.show();
        Client.loginStage.close();
    }


}
