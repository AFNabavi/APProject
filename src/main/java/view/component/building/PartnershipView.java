package view.component.building;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import view.ViewConstants;

public class PartnershipView extends BuildingView {
    
    public PartnershipView(Color color, String owner) {
        super("MVP", color, owner);
    }

    @Override // just for override, use next method.
    protected void draw() {}

    protected void draw(float x, float y) {
        Rectangle line = new Rectangle(x, y, ViewConstants.BOARD_SIDE, ViewConstants.PARTNERSHIP_THICK);
        line.setFill(color);
        getChildren().addAll(line);
    }
}
