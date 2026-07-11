import controller.GameController;
import view.GameView;
import view.SetupDialog;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application entry point. Shows the pre-game setup dialog, then hands
 * off to {@link GameView} once the player count / names / map size are
 * confirmed. Console-only execution is intentionally not supported:
 * the assignment requires a JavaFX GUI.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SetupDialog.show(primaryStage, (names, mapSize) -> {
            GameController controller = new GameController(names, mapSize);
            GameView view = new GameView(primaryStage, controller);
            view.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
