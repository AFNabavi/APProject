package view;

import model.GameEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Scrollable panel showing the running log of game events (dice rolls,
 * production, crises, builds, trades) as required by the spec.
 */
public class EventLogPanel extends VBox {

    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ListView<String> listView = new ListView<>(items);

    public EventLogPanel() {
        setSpacing(6);
        setPrefHeight(160);
        Label title = new Label("گزارش رویدادها");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        listView.setPrefHeight(140);
        getChildren().addAll(title, listView);
    }

    public void refresh(List<GameEvent> events) {
        items.setAll(events.stream().map(GameEvent::toString).toList());
    }

    public void append(String message) {
        items.add(0, message);
    }
}
