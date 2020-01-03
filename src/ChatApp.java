import com.chat.app.process.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("com/chat/app/view/login.fxml"));
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene((root));
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.show();
        Client.loginStage = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        Client.writer.println("bye");
        // Save file
    }
}
