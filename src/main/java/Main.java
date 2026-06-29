import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.component.TopBar;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("SILICON VALLEY");
        stage.setHeight(1024);
        stage.setWidth(1024);

        VBox root = new VBox();
        root.getChildren().add(new TopBar());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/board.css");

        stage.setScene(scene);
        stage.show();
    }
}