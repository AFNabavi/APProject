package controller;

/** The phases a normal turn goes through, plus the pre-game setup phase. */
public enum TurnPhase {
    SETUP,
    AWAITING_ROLL,
    ACTIONS,
    TURN_END,
    GAME_OVER
}
