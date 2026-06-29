package view.component;

import view.ViewConstants;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TopBar extends HBox {
    
    
    Button stopButton = new Button("STOP");
    Label gameName = new Label("Silicon Valley: The Tech Cartel");
    Label roundNum = new Label("ROUND NUMBER"); // TODO: import round number from model package.
    Label playerTurn = new Label("PLAYER TURN"); // TODO: import player number from model package.

    public TopBar() {
        super();
        this.getChildren().addAll(stopButton, gameName, roundNum, playerTurn);
        this.setMinHeight(ViewConstants.TOP_BAR_HEIGHT);
        this.setPrefHeight(ViewConstants.TOP_BAR_HEIGHT);
        this.setMaxHeight(ViewConstants.TOP_BAR_HEIGHT);
        this.getStyleClass().add("fx-top-bar");
        this.setPadding(new Insets(10));
        
        stopButton.setMaxWidth(100); // TODO: Solve it.

    }
}
