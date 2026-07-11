package com.silicontycoon.exception;

/**
 * Thrown by the SaveLoadManager when a save file cannot be parsed
 * because it is missing, truncated, or otherwise corrupted.
 */
public class CorruptedSaveException extends GameException {
    public CorruptedSaveException(String message) {
        super(message);
    }

    public CorruptedSaveException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
