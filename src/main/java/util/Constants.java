package com.silicontycoon.util;

/**
 * Central location for game-wide magic numbers, so no numeric literal
 * describing a rule appears scattered through the codebase.
 */
public final class Constants {
    private Constants() {}

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int WINNING_SCORE = 10;
    public static final int CRISIS_DICE_SUM = 7;
    public static final int MAX_HAND_BEFORE_TAX = 7;

    public static final String SAVE_FILE_EXTENSION = ".svg2";
    public static final String DEFAULT_SAVE_PATH = "savegame" + SAVE_FILE_EXTENSION;

    public static final String[] DEFAULT_PLAYER_COLORS = {
            "#e63946", // red
            "#457b9d", // blue
            "#2a9d8f", // teal
            "#f4a261"  // orange
    };
}
