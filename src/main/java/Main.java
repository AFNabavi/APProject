import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.component.TopBar;
import view.component.BottomBar;
import view.component.CenterPane;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("SILICON VALLEY");
        stage.setHeight(1024);
        stage.setWidth(1024);

        VBox root = new VBox();
        root.getChildren().addAll(new TopBar(), new CenterPane(), new BottomBar());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");

        stage.setScene(scene);
        stage.show();
    }
}