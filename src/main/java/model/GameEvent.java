package model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A single log entry describing something that happened during the game
 * (dice roll, production, construction, crisis, trade...). Displayed in
 * the UI's event report panel.
 */
public class GameEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { DICE, PRODUCTION, CRISIS, BUILD, TRADE, MARKET, ROLE, WIN, INFO, ERROR }

    private final Type type;
    private final String message;
    private final LocalDateTime timestamp;

    public GameEvent(Type type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + type + "] " + message;
    }
}
