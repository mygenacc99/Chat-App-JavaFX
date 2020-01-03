package com.chat.app.view;


import com.chat.app.process.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {

    @FXML
    private TextField tfName;

    public void loginButtonClick(ActionEvent actionEvent) throws IOException {
        String username = tfName.getText();
        Client.userName = username;
        Client.login = this;
        Client.init();
        Client.loginCommand();
    }

    public void loginFail(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login fail!");
        alert.setHeaderText("Login failed for user:" + Client.userName);
        alert.setContentText("The username already exists. \nPlease use a different username");

        alert.showAndWait();
    }

    public void loginSuccess() throws IOException {
        Stage chatStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));

        chatStage.setScene(new Scene(root));
        chatStage.show();
        Client.loginStage.close();
    }

}
