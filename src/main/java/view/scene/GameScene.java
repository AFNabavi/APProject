package view.scene;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import view.component.BottomBar;
import view.component.TopBar;
import view.component.board.Board;
import view.component.market.MarketPanel;
import view.component.player.PlayerPanel;

public class GameScene {
    
    public Scene init() {
        Board center = new Board();
        TopBar top = new TopBar();
        MarketPanel right = new MarketPanel();
        BottomBar bottom = new BottomBar();
        PlayerPanel left = new PlayerPanel();

        BorderPane root = new BorderPane(center, top, right, bottom, left);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/style.css");
        return scene;
    }
}
