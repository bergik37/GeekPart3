package lessons3.threeClients;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App3 extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent chat = FXMLLoader.load(getClass().getResource("app/sample.fxml"));
            primaryStage.setTitle("Chat_v0.1");
            primaryStage.setScene(new Scene(chat));
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception  {
        launch(args);
    }
}
