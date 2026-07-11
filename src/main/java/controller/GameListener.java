package com.silicontycoon.controller;

/**
 * Observer-pattern callback used by the view layer to react to engine
 * changes without polling. All notifications are dispatched on the
 * calling thread; the view is responsible for hopping back onto the
 * JavaFX Application Thread via {@code Platform.runLater} if needed.
 */
public interface GameListener {
    void onStateChanged();
    void onLog(String message);
}
