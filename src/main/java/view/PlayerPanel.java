package com.silicontycoon.view;

import com.silicontycoon.controller.GameController;
import com.silicontycoon.model.Player;
import com.silicontycoon.model.Resource;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.HBox;

/**
 * Shows every player's identity, total card count and (for the current
 * player only) the exact resource breakdown, matching the "hand is
 * private but count is public" rule.
 */
public class PlayerPanel extends VBox {

    private final GameController controller;

    public PlayerPanel(GameController controller) {
        this.controller = controller;
        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(240);
        refresh();
    }

    public void refresh() {
        getChildren().clear();
        Label title = new Label("بازیکنان");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        getChildren().add(title);

        Player current = controller.getState().getCurrentPlayer();
        for (Player p : controller.getState().getPlayers()) {
            HBox header = new HBox(6);
            Circle dot = new Circle(6, Color.web(p.getColorHex()));
            Label name = new Label(p.getName() + (p == current ? "  (نوبت فعلی)" : ""));
            name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            header.getChildren().addAll(dot, name);
            getChildren().add(header);

            int points = controller.getVictoryPoints(p);
            getChildren().add(new Label("امتیاز: " + points
                    + "   MVP: " + p.countMvps()
                    + "   Unicorn: " + p.countUnicorns()
                    + "   Partnership: " + p.countPartnerships()));
            if (p.getRole() != null) {
                getChildren().add(new Label("نقش: " + p.getRole().getEnglishName()));
            }
            getChildren().add(new Label("تعداد کارت‌ها: " + p.getTotalResourceCount()));

            if (p == current) {
                StringBuilder sb = new StringBuilder();
                for (Resource r : Resource.values()) {
                    sb.append(r.getPersianName()).append(": ").append(p.getResourceCount(r)).append("   ");
                }
                Label hand = new Label(sb.toString());
                hand.setWrapText(true);
                getChildren().add(hand);
            }
            getChildren().add(new Separator());
        }
    }
}
