package view.component.market;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import view.ViewConstants;
import view.component.style.ElementStyle;

public class MarketPanel extends VBox {

    private Label test = new Label("test in market panel");
    private Label[] labels = {test};

    public MarketPanel() {
        super();
        setSize();
        this.getChildren().addAll(test);
        this.getStyleClass().addAll("fx-market-panel", "fx-pane-border");
        ElementStyle.setLabelPos(this);
        ElementStyle.setCssStyle("fx-panel-label", labels);
    }
    
    public void setSize() {
        this.setMaxWidth(ViewConstants.MARKET_PANEL_WIDTH);
        this.setMinWidth(ViewConstants.MARKET_PANEL_WIDTH);
        this.setPrefHeight(ViewConstants.MARKET_PANEL_HEIGHT);
    }

}
