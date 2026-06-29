package view.component;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import view.ViewConstants;

public class BottomBar extends HBox {
    
    Button roleDice = new Button("ROLE");
    Button build = new Button("BUILD");
    Button trade = new Button("TRADE");
    // TODO: add some eles...

    public BottomBar() {
        super();
        setSize();
        this.getChildren().addAll(roleDice, build, trade);
        this.getStyleClass().add("fx-bottom-bar");
    }

    private void setSize() {
        this.setMaxHeight(ViewConstants.BOTTOM_BAR_HEIGHT);
        this.setMinHeight(ViewConstants.BOTTOM_BAR_HEIGHT);
        this.setPrefWidth(ViewConstants.BOTTOM_BAR_WIDTH);
    }
}
