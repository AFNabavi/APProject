package view.component.board;

import javafx.scene.layout.VBox;
import view.ViewConstants;

public class Board extends VBox {
    
    public Board() {
        super();
        setSize();
        this.getStyleClass().addAll("fx-board", "fx-pane-border");
        this.getChildren().add(new InfoPanel());
    }

    private void setSize() {
        this.setMaxWidth(ViewConstants.BOARD_WIDTH);
        this.setMinWidth(ViewConstants.BOARD_WIDTH);
        this.setPrefHeight(ViewConstants.BOARD_HEIGHT);
    }
}
