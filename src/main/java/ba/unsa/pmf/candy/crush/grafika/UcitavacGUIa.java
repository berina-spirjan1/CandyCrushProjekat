package ba.unsa.pmf.candy.crush.grafika;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UcitavacGUIa extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getFxmlResource("prozor"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Candy crush");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private URL getFxmlResource(String name) {
        String path = "/fxml/" + name + ".fxml";
        return UcitavacGUIa.class.getResource(path);
    }
}
