package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Pre-game configuration screen: number of players (2-4), their names
 * and the map size. Calls back with the collected data once the user
 * confirms.
 */
public class SetupDialog {

    public static void show(Stage stage, BiConsumer<List<String>, Integer> onConfirm) {
        VBox root = new VBox(14);
        root.setPadding(new Insets(24));

        Label title = new Label("Silicon Valley: The Tech Cartel — تنظیمات بازی");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label countLabel = new Label("تعداد بازیکنان:");
        ComboBox<Integer> countBox = new ComboBox<>();
        countBox.getItems().addAll(2, 3, 4);
        countBox.setValue(2);

        GridPane namesGrid = new GridPane();
        namesGrid.setHgap(8);
        namesGrid.setVgap(8);
        List<TextField> nameFields = new ArrayList<>();

        Label sizeLabel = new Label("سایز نقشه (پیش‌فرض ۵ یعنی 5x5):");
        Spinner<Integer> sizeSpinner = new Spinner<>(4, 8, 5);

        Button confirm = new Button("شروع بازی");
        Label error = new Label();
        error.setStyle("-fx-text-fill: red;");

        java.util.function.Consumer<Integer> buildNameFields = (n) -> {
            namesGrid.getChildren().clear();
            nameFields.clear();
            for (int i = 0; i < n; i++) {
                Label l = new Label("نام بازیکن " + (i + 1) + ":");
                TextField tf = new TextField("Player " + (i + 1));
                namesGrid.add(l, 0, i);
                namesGrid.add(tf, 1, i);
                nameFields.add(tf);
            }
        };
        buildNameFields.accept(countBox.getValue());
        countBox.valueProperty().addListener((obs, old, val) -> buildNameFields.accept(val));

        confirm.setOnAction(e -> {
            List<String> names = new ArrayList<>();
            for (TextField tf : nameFields) {
                String n = tf.getText() == null || tf.getText().isBlank() ? "Player" : tf.getText().trim();
                names.add(n);
            }
            onConfirm.accept(names, sizeSpinner.getValue());
        });

        root.getChildren().addAll(title, countLabel, countBox, namesGrid, sizeLabel, sizeSpinner, confirm, error);
        Scene scene = new Scene(root, 480, 480);
        stage.setScene(scene);
        stage.setTitle("Silicon Valley: The Tech Cartel");
        stage.show();
    }
}
