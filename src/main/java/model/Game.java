package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Board board;

    private List<Player> players = new ArrayList<>();

    private Market market;

    private int currentPlayerIndex;

    public Game(Board board, Market market) {
        this.board = board;
        this.market = market;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Market getMarket() {
        return market;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
}