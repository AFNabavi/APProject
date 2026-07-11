package exception;

/**
 * Thrown when a player attempts an action (build, upgrade, trade) that
 * costs more resources than they currently hold.
 */
public class InsufficientResourcesException extends GameException {
    public InsufficientResourcesException(String message) {
        super(message);
    }
}
