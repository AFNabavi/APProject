package com.silicontycoon.view;

import com.silicontycoon.controller.GameController;
import com.silicontycoon.model.Market;
import com.silicontycoon.model.Resource;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * Displays live market prices for the five resources and offers a
 * one-click "buy" button for each (spends Capital at the current price).
 */
public class MarketPanel extends VBox {

    private final GameController controller;
    private Consumer<Resource> onBuy = r -> {};

    public MarketPanel(GameController controller) {
        this.controller = controller;
        setSpacing(6);
        setPadding(new Insets(10));
        setPrefWidth(240);
        refresh();
    }

    public void setOnBuy(Consumer<Resource> onBuy) { this.onBuy = onBuy; }

    public void refresh() {
        getChildren().clear();
        Label title = new Label("بازار پویا");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        getChildren().add(title);

        Market market = controller.getState().getMarket();
        for (Resource r : Resource.values()) {
            HBox row = new HBox(8);
            Label label = new Label(r.getPersianName() + ": " + market.getPrice(r) + " سرمایه");
            Button buy = new Button("خرید");
            buy.setOnAction(e -> onBuy.accept(r));
            row.getChildren().addAll(label, buy);
            getChildren().add(row);
        }
    }
}
