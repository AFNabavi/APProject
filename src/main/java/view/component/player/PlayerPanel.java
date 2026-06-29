package view.component.player;

import view.ViewConstants;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerPanel extends VBox {
    
    Label test = new Label("test label \nin player panel");

    public PlayerPanel() {
        super();
        setSize();
        this.getChildren().addAll(test);
        this.getStyleClass().add("fx-player-panel");
    }

    private void setSize() {
        this.setMaxWidth(ViewConstants.PLAYER_PANEL_WIDTH);
        this.setMinWidth(ViewConstants.PLAYER_PANEL_WIDTH);
        this.setPrefHeight(ViewConstants.PLAYER_PANEL_HEIGHT);
    }
}
