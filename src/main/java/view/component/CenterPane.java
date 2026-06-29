package view.component;

import view.ViewConstants;
import view.component.board.Board;
import view.component.market.MarketPanel;
import view.component.player.PlayerPanel;
import javafx.scene.layout.HBox;

public class CenterPane extends HBox {

    Board board = new Board();
    MarketPanel market = new MarketPanel();
    PlayerPanel player = new PlayerPanel();

    public CenterPane() {
        super();
        setSize();
        this.getChildren().addAll(player, board, market);
        this.getStyleClass().add("fx-center-pane");
    }

    private void setSize() {
        this.setMinHeight(ViewConstants.CENTER_PANE_HEIGHT);
        this.setMaxHeight(ViewConstants.CENTER_PANE_HEIGHT);
        this.setPrefWidth(ViewConstants.CENTER_PANE_WIDTH);
    }
     
}
