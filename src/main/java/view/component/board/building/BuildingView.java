package view.component.board.building;

import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import config.BuildingType;

public abstract class BuildingView extends Pane {

    protected BuildingType type;
    protected Color color;
    protected String owner;

    public BuildingView(String type, Color color, String owner) {
        setType(type);
        this.color = color;
        this.owner = owner;

        draw();
    }

    protected abstract void draw();

    protected void setType(String inp) {
        inp = inp.toUpperCase();
        switch (inp) {
            case "MVP":
                type = BuildingType.MVP;
                break;
            case "UNICORN":
                type = BuildingType.UNICORN;
                break;
            case "PARTNERSHIP":
                type = BuildingType.PARTNERSHIP;
                break;
        }
    }
    public String getType() { return type.toString(); }

    public Color getColor() { return color; }

    public String getOwner() { return owner; }
}
