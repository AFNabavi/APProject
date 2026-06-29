package view.component.building;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import view.ViewConstants;

public class UnicornView extends BuildingView {

    public UnicornView(Color color, String owner) {
        super("UNICORN", color, owner);
    }

    @Override // just for override, use next method.
    protected void draw() {}

    protected void draw(float x, float y) {
        Circle circle = new Circle(x, y, ViewConstants.UNICORN_RAIDUS);
        circle.setFill(color);  // TODO: color is incorrect, it depended on type.

        Circle border = new Circle(x, y, ViewConstants.UNICORN_RAIDUS+ViewConstants.BUILDING_BORDER_THICK);
        border.setFill(color);

        getChildren().addAll(circle, border);
    }
}
