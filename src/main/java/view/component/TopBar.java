package view.component;

import view.ViewConstants;
import view.component.style.ElementStyle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class TopBar extends HBox {
    
    Button stopButton = new Button("STOP");
    Label gameName = new Label("Silicon Valley: The Tech Cartel");
    Label roundNum = new Label("ROUND NUMBER"); // TODO: import round number from model package.
    Label playerTurn = new Label("PLAYER TURN"); // TODO: import player number from model package.
    Label[] labels = {gameName, playerTurn, roundNum};
    // byte labelCount = 3;

    public TopBar() {
        super();
        setSize();
        this.getChildren().addAll(gameName, roundNum, playerTurn, stopButton);
        this.getStyleClass().addAll("fx-top-bar", "fx-pane-border");
        // this.setPadding(new Insets(10));
        
        stopButton.setMaxWidth(100); // TODO: Solve it.
        ElementStyle.setLabelPos(this);
        ElementStyle.setCssStyle("fx-top-bar-label", labels);
        ElementStyle.setCssStyle("fx-bar-button", stopButton);
        ElementStyle.setMargin(this, labels, stopButton);
    }

    private void setSize() {
        this.setMinHeight(ViewConstants.TOP_BAR_HEIGHT);
        this.setMaxHeight(ViewConstants.TOP_BAR_HEIGHT);
        this.setPrefWidth(ViewConstants.TOP_BAR_WIDTH);
    }

}
