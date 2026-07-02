package view.component.style;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Labeled;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ElementStyle {

    public static void setMargin(Labeled... elements) {
        Insets insets = new Insets(0, 10, 0, 10);
        for (Labeled l : elements)
            VBox.setMargin(l, insets);
    }

    // public static void setMargin(Labeled... elements) {
    //     Insets insets = new Insets(0, 10, 0, 10);
    //     for (Labeled l : elements)
    //         HBox.setMargin(l, insets);
    // }

    public static void setMargin(Labeled[] labels, Labeled... elements) {
        Insets insets = new Insets(0, 10, 0, 10);
        for (Labeled l : elements)
            VBox.setMargin(l, insets);
        for (Labeled l : labels)
            VBox.setMargin(l, insets);
    }
    
    // public static void setMargin(Labeled[] labels, Labeled... elements) {
    //     Insets insets = new Insets(0, 10, 0, 10);
    //     for (Labeled l : elements)
    //         node.setMargin(l, insets);
    //     for (Labeled l : labels)
    //         node.setMargin(l, insets);
    // }

    public static void setLabelPos(VBox node) { node.setAlignment(Pos.CENTER); }

    public static void setLabelPos(HBox node) { node.setAlignment(Pos.CENTER); }

    public static void setCssStyle(String cssStyle, Labeled... labels) {
        for (Labeled l : labels)
            l.getStyleClass().add(cssStyle);
    }
}