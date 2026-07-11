package com.silicontycoon.view;

import com.silicontycoon.controller.GameController;
import com.silicontycoon.controller.GameListener;
import com.silicontycoon.controller.TurnPhase;
import com.silicontycoon.exception.CorruptedSaveException;
import com.silicontycoon.model.FounderRole;
import com.silicontycoon.model.Player;
import com.silicontycoon.model.Resource;
import com.silicontycoon.util.Constants;
import com.silicontycoon.util.SaveLoadManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Assembles and drives the whole in-game screen: board, side panels,
 * toolbar and the (small) client-side state machine that turns raw
 * mouse clicks on the board into controller calls. Game-logic-heavy
 * calls (dice resolution, save/load) are dispatched to a background
 * executor; every UI mutation coming back from that thread is wrapped
 * in {@code Platform.runLater}, per the threading requirement.
 */
public class GameView {

    private enum PendingAction { NONE, SETUP_PLACEMENT, BUILD_MVP, UPGRADE, BUILD_PARTNERSHIP, MOVE_AUDITOR }

    private final Stage stage;
    private final GameController controller;
    private final ExecutorService logicExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "game-logic-thread");
        t.setDaemon(true);
        return t;
    });

    private BoardView boardView;
    private PlayerPanel playerPanel;
    private MarketPanel marketPanel;
    private EventLogPanel eventLogPanel;
    private Label statusLabel;
    private Button rollButton;
    private Button endTurnButton;

    private PendingAction pendingAction = PendingAction.NONE;
    private Integer pendingVertex = null;

    public GameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        BorderPane root = new BorderPane();

        boardView = new BoardView(controller.getState().getMap());
        boardView.setOnVertexClicked(this::handleVertexClick);
        boardView.setOnEdgeClicked(this::handleEdgeClick);
        boardView.setOnSectorClicked(this::handleSectorClick);
        ScrollPane boardScroll = new ScrollPane(boardView);
        boardScroll.setPannable(true);
        root.setCenter(boardScroll);

        playerPanel = new PlayerPanel(controller);
        marketPanel = new MarketPanel(controller);
        marketPanel.setOnBuy(this::handleBuy);
        VBox rightBox = new VBox(10, playerPanel, new Separator(), marketPanel);
        rightBox.setPadding(new Insets(6));
        root.setRight(rightBox);

        eventLogPanel = new EventLogPanel();
        eventLogPanel.setPadding(new Insets(6));
        root.setBottom(buildBottomArea());

        statusLabel = new Label();
        statusLabel.setPadding(new Insets(8));
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        root.setTop(buildTopArea());

        controller.addListener(new GameListener() {
            @Override public void onStateChanged() { Platform.runLater(GameView.this::refreshAll); }
            @Override public void onLog(String message) { Platform.runLater(() -> eventLogPanel.append(message)); }
        });

        Scene scene = new Scene(root, 1180, 820);
        stage.setScene(scene);
        stage.setTitle("Silicon Valley: The Tech Cartel");
        stage.show();

        beginSetupPhase();
        refreshAll();
    }

    private BorderPane buildTopArea() {
        BorderPane top = new BorderPane();
        Label title = new Label("Silicon Valley: The Tech Cartel");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        top.setLeft(title);
        Label status = new Label();
        status.setPadding(new Insets(6));
        top.setCenter(status);
        this.statusLabel = status;
        return top;
    }

    private VBox buildBottomArea() {
        VBox bottom = new VBox(6);
        bottom.setPadding(new Insets(6));

        rollButton = new Button("انداختن تاس");
        rollButton.setOnAction(e -> doRollDice());

        Button buildPartnershipBtn = new Button("ساخت Partnership");
        buildPartnershipBtn.setOnAction(e -> startPendingAction(PendingAction.BUILD_PARTNERSHIP, "یک یال خالی متصل به شبکه خود انتخاب کنید."));

        Button buildMvpBtn = new Button("ساخت MVP");
        buildMvpBtn.setOnAction(e -> startPendingAction(PendingAction.BUILD_MVP, "یک گره خالی و مجاز انتخاب کنید."));

        Button upgradeBtn = new Button("ارتقا به Unicorn");
        upgradeBtn.setOnAction(e -> startPendingAction(PendingAction.UPGRADE, "یک MVP متعلق به خودتان را انتخاب کنید."));

        Button roleBtn = new Button("انتخاب نقش بنیان‌گذار");
        roleBtn.setOnAction(e -> chooseRoleDialog());

        endTurnButton = new Button("پایان نوبت");
        endTurnButton.setOnAction(e -> {
            controller.endTurn();
            checkForWinner();
        });

        Button saveBtn = new Button("ذخیره بازی");
        saveBtn.setOnAction(e -> doSave());

        Button loadBtn = new Button("بارگذاری بازی");
        loadBtn.setOnAction(e -> doLoad());

        HBox toolbar = new HBox(8, rollButton, buildPartnershipBtn, buildMvpBtn, upgradeBtn, roleBtn, endTurnButton, saveBtn, loadBtn);
        bottom.getChildren().addAll(toolbar, eventLogPanel);
        return bottom;
    }

    // ---------------------------------------------------------------
    // Setup phase
    // ---------------------------------------------------------------

    private void beginSetupPhase() {
        pendingAction = PendingAction.SETUP_PLACEMENT;
        updateStatus("فاز راه‌اندازی: نوبت " + controller.getState().getCurrentPlayer().getName()
                + " — یک گره برای MVP انتخاب کنید.");
    }

    private void chooseRoleDialog() {
        Player current = controller.getState().getCurrentPlayer();
        if (current.getRole() != null) {
            new Alert(Alert.AlertType.INFORMATION, "این بازیکن قبلا نقش انتخاب کرده است.").showAndWait();
            return;
        }
        ChoiceDialog<FounderRole> dialog = new ChoiceDialog<>(FounderRole.HACKER_CEO, FounderRole.values());
        dialog.setTitle("انتخاب نقش بنیان‌گذار");
        dialog.setHeaderText("انتخاب نقش یک امتیاز از شما کم می‌کند اما مزیت ویژه می‌دهد.");
        dialog.setContentText("نقش:");
        dialog.showAndWait().ifPresent(role -> controller.chooseRole(current, role));
    }

    // ---------------------------------------------------------------
    // Board interaction
    // ---------------------------------------------------------------

    private void startPendingAction(PendingAction action, String hint) {
        if (controller.getPhase() != TurnPhase.ACTIONS) {
            showError("ابتدا باید تاس بیندازید.");
            return;
        }
        this.pendingAction = action;
        updateStatus(hint);
    }

    private void handleVertexClick(int vertexId) {
        try {
            switch (pendingAction) {
                case SETUP_PLACEMENT:
                    pendingVertex = vertexId;
                    updateStatus("حالا یک یال متصل به این گره برای Partnership انتخاب کنید.");
                    break;
                case BUILD_MVP:
                    controller.buildMVP(vertexId);
                    pendingAction = PendingAction.NONE;
                    updateStatus("MVP ساخته شد.");
                    break;
                case UPGRADE:
                    controller.upgradeToUnicorn(vertexId);
                    pendingAction = PendingAction.NONE;
                    updateStatus("ارتقا انجام شد.");
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleEdgeClick(int edgeId) {
        try {
            switch (pendingAction) {
                case SETUP_PLACEMENT:
                    if (pendingVertex == null) {
                        showError("ابتدا یک گره برای MVP انتخاب کنید.");
                        return;
                    }
                    controller.placeInitialCompany(pendingVertex, edgeId);
                    pendingVertex = null;
                    if (controller.getState().isSetupPhase()) {
                        updateStatus("نوبت " + controller.getState().getCurrentPlayer().getName()
                                + " — یک گره برای MVP انتخاب کنید.");
                    } else {
                        pendingAction = PendingAction.NONE;
                        updateStatus("فاز راه‌اندازی تمام شد. " + controller.getState().getCurrentPlayer().getName()
                                + " تاس بیندازید.");
                    }
                    break;
                case BUILD_PARTNERSHIP:
                    controller.buildPartnership(edgeId);
                    pendingAction = PendingAction.NONE;
                    updateStatus("Partnership ساخته شد.");
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleSectorClick(int encoded) {
        if (pendingAction != PendingAction.MOVE_AUDITOR) return;
        int row = encoded / 100;
        int col = encoded % 100;
        try {
            controller.moveAuditor(row, col);
            pendingAction = PendingAction.NONE;
            updateStatus("بازرس منتقل شد.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void handleBuy(Resource r) {
        try {
            controller.buyFromMarket(r);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Dice / crisis flow (runs off the FX thread)
    // ---------------------------------------------------------------

    private void doRollDice() {
        rollButton.setDisable(true);
        logicExecutor.submit(() -> {
            try {
                int sum = controller.rollDice();
                Platform.runLater(() -> {
                    rollButton.setDisable(false);
                    if (sum == Constants.CRISIS_DICE_SUM) {
                        pendingAction = PendingAction.MOVE_AUDITOR;
                        updateStatus("بحران قانونی! یک سکتور برای انتقال بازرس انتخاب کنید.");
                    } else {
                        updateStatus("مجموع تاس: " + sum + " — اقدامات این نوبت را انجام دهید یا نوبت را پایان دهید.");
                    }
                    refreshAll();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> { rollButton.setDisable(false); showError(ex.getMessage()); });
            }
        });
    }

    // ---------------------------------------------------------------
    // Save / Load (background thread, per spec)
    // ---------------------------------------------------------------

    private void doSave() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(Constants.DEFAULT_SAVE_PATH);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Silicon Valley Save", "*" + Constants.SAVE_FILE_EXTENSION));
        File file = chooser.showSaveDialog(stage);
        if (file == null) return;
        updateStatus("در حال ذخیره بازی...");
        SaveLoadManager.saveAsync(controller.getState(), file,
                () -> Platform.runLater(() -> updateStatus("بازی با موفقیت ذخیره شد.")),
                ex -> Platform.runLater(() -> showError("خطا در ذخیره‌سازی: " + ex.getMessage())));
    }

    private void doLoad() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Silicon Valley Save", "*" + Constants.SAVE_FILE_EXTENSION));
        File file = chooser.showOpenDialog(stage);
        if (file == null) return;
        updateStatus("در حال بارگذاری بازی...");
        SaveLoadManager.loadAsync(file,
                loadedState -> Platform.runLater(() -> {
                    controller.loadState(loadedState);
                    pendingAction = controller.getState().isSetupPhase() ? PendingAction.SETUP_PLACEMENT : PendingAction.NONE;
                    pendingVertex = null;
                    refreshAll();
                    updateStatus("بازی با موفقیت بارگذاری شد.");
                }),
                ex -> Platform.runLater(() -> {
                    if (ex instanceof CorruptedSaveException) {
                        showError("فایل ذخیره خراب است: " + ex.getMessage());
                    } else {
                        showError("خطا در بارگذاری: " + ex.getMessage());
                    }
                }));
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void checkForWinner() {
        Player winner = controller.getState().getWinner();
        if (winner != null) {
            new Alert(Alert.AlertType.INFORMATION, winner.getName() + " برنده بازی شد!").showAndWait();
        }
    }

    private void refreshAll() {
        boardView.setMap(controller.getState().getMap());
        playerPanel.refresh();
        marketPanel.refresh();
        eventLogPanel.refresh(controller.getState().getEventLog());
    }

    private void updateStatus(String text) {
        statusLabel.setText(text);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message == null ? "خطای نامشخص" : message);
        alert.showAndWait();
    }
}
