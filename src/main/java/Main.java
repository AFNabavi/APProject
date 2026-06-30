import javafx.application.Application;
import javafx.stage.Stage;
import view.scene.GameScene;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SILICON VALLEY");
        primaryStage.setHeight(1024);
        primaryStage.setWidth(1024);
        primaryStage.setResizable(false);

        GameScene gameScene = new GameScene();
        primaryStage.setScene(gameScene.init());
        primaryStage.show();
    }
}