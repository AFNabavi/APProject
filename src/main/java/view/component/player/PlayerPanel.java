package view.component.player;

import view.ViewConstants;
import view.component.style.ElementStyle;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerPanel extends VBox {
    
    private Label test = new Label("test label \nin player panel");
    private Label[] labels = {test};

    public PlayerPanel() {
        super();
        setSize();
        this.getChildren().addAll(test);
        this.getStyleClass().addAll("fx-player-panel", "fx-pane-border");
        ElementStyle.setLabelPos(this);
        ElementStyle.setCssStyle("fx-panel-label", labels);
    }

    private void setSize() {
        this.setMaxWidth(ViewConstants.PLAYER_PANEL_WIDTH);
        this.setMinWidth(ViewConstants.PLAYER_PANEL_WIDTH);
        this.setPrefHeight(ViewConstants.PLAYER_PANEL_HEIGHT);
    }

}
