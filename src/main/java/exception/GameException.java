package com.silicontycoon.exception;

/**
 * Base class for all custom checked exceptions raised by the game engine.
 */
public abstract class GameException extends Exception {
    protected GameException(String message) {
        super(message);
    }
}
