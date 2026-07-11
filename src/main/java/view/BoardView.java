package com.silicontycoon.view;

import com.silicontycoon.model.*;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.IntConsumer;

/**
 * Renders the Tech Park board: sectors as coloured tiles (Canvas), and
 * vertices / edges as interactive JavaFX shapes layered on top so the
 * user can click them to build companies and partnerships. Purely a
 * presentation component - it never mutates the model, only reads it
 * and forwards clicks to the controller via callbacks.
 */
public class BoardView extends Pane {

    public static final int CELL_SIZE = 88;
    public static final int OFFSET = 46;
    private static final double VERTEX_RADIUS = 11;
    private static final double EDGE_WIDTH = 8;

    private final Canvas canvas;
    private GameMap map;

    private IntConsumer onVertexClicked = id -> {};
    private IntConsumer onEdgeClicked = id -> {};
    private IntConsumer onSectorClicked = id -> {}; // encoded row*100+col

    public BoardView(GameMap map) {
        this.map = map;
        int size = map.getSize();
        double dim = OFFSET * 2 + CELL_SIZE * (size - 1) + CELL_SIZE;
        canvas = new Canvas(dim, dim);
        setPrefSize(dim, dim);
        getChildren().add(canvas);
        redraw();
    }

    public void setOnVertexClicked(IntConsumer c) { this.onVertexClicked = c; }
    public void setOnEdgeClicked(IntConsumer c) { this.onEdgeClicked = c; }
    public void setOnSectorClicked(IntConsumer c) { this.onSectorClicked = c; }

    public void setMap(GameMap map) {
        this.map = map;
        redraw();
    }

    /** Full re-render: sectors on the canvas, vertices/edges as shape nodes. */
    public void redraw() {
        getChildren().setAll(canvas);
        drawSectors();
        drawEdges();
        drawVertices();
    }

    private double sectorX(int col) { return OFFSET + col * CELL_SIZE; }
    private double sectorY(int row) { return OFFSET + row * CELL_SIZE; }
    private double vertexX(Vertex v) { return OFFSET + (v.getGridCol() + 1) * CELL_SIZE; }
    private double vertexY(Vertex v) { return OFFSET + (v.getGridRow() + 1) * CELL_SIZE; }

    private void drawSectors() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Auditor auditor = map.getAuditor();
        for (int r = 0; r < map.getSize(); r++) {
            for (int c = 0; c < map.getSize(); c++) {
                Sector sector = map.getSector(r, c);
                double x = sectorX(c);
                double y = sectorY(r);
                gc.setFill(colorFor(sector.getType()));
                gc.fillRoundRect(x + 3, y + 3, CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);
                gc.setStroke(Color.web("#2b2b2b"));
                gc.setLineWidth(1.4);
                gc.strokeRoundRect(x + 3, y + 3, CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
                gc.fillText(shortLabel(sector.getType()), x + 10, y + 22);
                if (!sector.getType().isNeutral()) {
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    gc.fillText(String.valueOf(sector.getActivationNumber()), x + CELL_SIZE / 2.0 - 8, y + CELL_SIZE / 2.0 + 18);
                }
                if (auditor.isOn(r, c)) {
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(3);
                    gc.strokeOval(x + 10, y + CELL_SIZE - 26, 16, 16);
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x + 10, y + CELL_SIZE - 26, 16, 16);
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                    gc.fillText("A", x + 14, y + CELL_SIZE - 15);
                }
            }
        }
    }

    private void drawEdges() {
        for (Edge edge : map.getEdges()) {
            Vertex a = map.getVertex(edge.getVertexA());
            Vertex b = map.getVertex(edge.getVertexB());
            Line line = new Line(vertexX(a), vertexY(a), vertexX(b), vertexY(b));
            line.setStrokeWidth(EDGE_WIDTH);
            if (edge.isOccupied()) {
                line.setStroke(Color.web(edge.getPartnership().getOwner().getColorHex()));
            } else {
                line.setStroke(Color.web("#cccccc"));
            }
            line.setPickOnBounds(true);
            line.setOnMouseClicked(e -> onEdgeClicked.accept(edge.getId()));
            getChildren().add(line);
        }
    }

    private void drawVertices() {
        for (Vertex vertex : map.getVertices()) {
            Circle circle = new Circle(vertexX(vertex), vertexY(vertex), VERTEX_RADIUS);
            if (vertex.isOccupied()) {
                CompanyStructure company = vertex.getCompany();
                circle.setFill(Color.web(company.getOwner().getColorHex()));
                circle.setStroke(company instanceof Unicorn ? Color.GOLD : Color.BLACK);
                circle.setStrokeWidth(company instanceof Unicorn ? 3 : 1.5);
            } else {
                circle.setFill(Color.web("#f1f1f1"));
                circle.setStroke(Color.web("#555555"));
            }
            circle.setOnMouseClicked(e -> onVertexClicked.accept(vertex.getId()));
            getChildren().add(circle);
        }
    }

    private String shortLabel(SectorType type) {
        switch (type) {
            case AI_HUB: return "AI Hub";
            case FINTECH_DISTRICT: return "Fintech";
            case CLOUD_CAMPUS: return "Cloud";
            case IP_QUARTER: return "IP Q.";
            case DATA_VALLEY: return "Data";
            default: return "Regulatory";
        }
    }

    private Color colorFor(SectorType type) {
        switch (type) {
            case AI_HUB: return Color.web("#6c5ce7");
            case FINTECH_DISTRICT: return Color.web("#00b894");
            case CLOUD_CAMPUS: return Color.web("#0984e3");
            case IP_QUARTER: return Color.web("#e17055");
            case DATA_VALLEY: return Color.web("#d63031");
            default: return Color.web("#636e72");
        }
    }
}
