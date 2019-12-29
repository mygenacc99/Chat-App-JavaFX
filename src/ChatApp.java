import com.chat.app.process.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Client.loginStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("com/chat/app/view/login.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Client.init();
        launch(args);
    }

    @Override
    public void stop(){
        Client.writer.println("bye");
        // Save file
    }
}