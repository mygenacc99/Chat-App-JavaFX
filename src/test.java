import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList("Cricket", "Chess", "Kabaddy", "Badminton",
                "Football", "Golf", "CoCo", "car racing");
        listView.setItems(items);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label label = new Label();
        label.setTextFill(Color.RED);

        listView.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    ObservableList<String> selectedItems = listView.getSelectionModel().getSelectedItems();

                    StringBuilder builder = new StringBuilder("Selected items :");

                    for (String name : selectedItems) {
                        builder.append(name + "\n");
                    }

                    label.setText(builder.toString());

                });

        HBox hBox = new HBox(30, listView, label);

        Scene scene = new Scene(hBox, 600, 300);

        /* Set the scene to primaryStage, and call the show method */
        primaryStage.setTitle("JavaFX ListView app Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
class TestFX {
    public static void main(String args[]) {
        Application.launch(test.class, args);
    }
}
