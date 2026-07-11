package com.silicontycoon.exception;

/**
 * Thrown when a player attempts to build a structure on an illegal
 * location: an occupied vertex/edge, a vertex that violates the
 * 2-edge distance rule, or a Partnership that is not connected to the
 * player's existing network.
 */
public class InvalidPlacementException extends GameException {
    public InvalidPlacementException(String message) {
        super(message);
    }
}
