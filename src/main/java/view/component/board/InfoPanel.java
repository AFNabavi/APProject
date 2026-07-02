package view.component.board;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import view.ViewConstants;

public class InfoPanel extends HBox {
    private static Image vertexIcon = new Image("images/sector_info.png");
    private static ImageView vertexImage = new ImageView(vertexIcon);

    private static Image helpIcon = new Image("images/help_info.png");
    private static ImageView helpImage = new ImageView(helpIcon);
    
    public InfoPanel() {
        setHeight(ViewConstants.INFO_PANEL_HEIGHT);
        setWidth(ViewConstants.INFO_PANEL_WIDTH);
        setPadding(new Insets(2, 0, 2, 0));
        getStyleClass().add("fx-info-panel");
        this.getChildren().addAll(vertexImage, helpImage);
    }
}
