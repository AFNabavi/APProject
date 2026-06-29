package view.component.market;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import view.ViewConstants;

public class MarketPanel extends VBox {

    Label test = new Label("test in market panel");

    public MarketPanel() {
        super();
        setSize();
        this.getChildren().addAll(test);
        this.getStyleClass().add("fx-market-panel");
    }
    
    public void setSize() {
        this.setMaxWidth(ViewConstants.MARKET_PANEL_WIDTH);
        this.setMinWidth(ViewConstants.MARKET_PANEL_WIDTH);
        this.setPrefHeight(ViewConstants.MARKET_PANEL_HEIGHT);
    }
}
