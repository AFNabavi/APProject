package view.component;

import view.component.style.ElementStyle;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import view.ViewConstants;

public class BottomBar extends HBox {
    
    Button roleDice = new Button("ROLE");
    Button build = new Button("BUILD");
    Button trade = new Button("TRADE");
    Labeled[] elements = {roleDice, build, trade};
    // TODO: add some eles...

    public BottomBar() {
        super();
        setSize();
        this.getChildren().addAll(roleDice, build, trade);
        this.getStyleClass().addAll("fx-bottom-bar", "fx-pane-border");
        ElementStyle.setLabelPos(this);
        ElementStyle.setCssStyle("fx-bar-button", elements);
        ElementStyle.setMargin(this, elements);
    }

    private void setSize() {
        this.setMaxHeight(ViewConstants.BOTTOM_BAR_HEIGHT);
        this.setMinHeight(ViewConstants.BOTTOM_BAR_HEIGHT);
        this.setPrefWidth(ViewConstants.BOTTOM_BAR_WIDTH);
    }
}
