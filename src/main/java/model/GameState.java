package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The complete, serializable snapshot of an in-progress game: the map,
 * players, market, dice, event log and whose turn it currently is. This
 * single object is what gets written to / read from disk by the
 * SaveLoadManager.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private GameMap map;
    private List<Player> players = new ArrayList<>();
    private Market market = new Market();
    private Dice dice = new Dice();
    private int currentPlayerIndex = 0;
    private boolean setupPhase = true;
    private int setupRound = 1; // 1 = forward round, 2 = reverse round
    private LinkedList<GameEvent> eventLog = new LinkedList<>();
    private Player winner;
    private Player longestNetworkHolder;

    public Player getLongestNetworkHolder() { return longestNetworkHolder; }
    public void setLongestNetworkHolder(Player p) { this.longestNetworkHolder = p; }

    public GameMap getMap() { return map; }
    public void setMap(GameMap map) { this.map = map; }

    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }

    public Market getMarket() { return market; }
    public Dice getDice() { return dice; }

    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int i) { this.currentPlayerIndex = i; }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isSetupPhase() { return setupPhase; }
    public void setSetupPhase(boolean setupPhase) { this.setupPhase = setupPhase; }

    public int getSetupRound() { return setupRound; }
    public void setSetupRound(int setupRound) { this.setupRound = setupRound; }

    public LinkedList<GameEvent> getEventLog() { return eventLog; }

    public void log(GameEvent.Type type, String message) {
        eventLog.addFirst(new GameEvent(type, message));
        while (eventLog.size() > 200) {
            eventLog.removeLast();
        }
    }

    public Player getWinner() { return winner; }
    public void setWinner(Player winner) { this.winner = winner; }

    public static final int MIN_NETWORK_LENGTH_FOR_BONUS = 3;

    /**
     * Finds which player, if any, currently holds the longest network
     * bonus. A chain must be at least {@link #MIN_NETWORK_LENGTH_FOR_BONUS}
     * partnerships long to qualify; ties keep the previous holder.
     */
    public Player computeLongestNetworkHolder(Player previousHolder) {
        int bestLength = MIN_NETWORK_LENGTH_FOR_BONUS - 1;
        Player holder = null;
        for (Player p : players) {
            int len = map.longestNetworkFor(p);
            if (len > bestLength) {
                bestLength = len;
                holder = p;
            } else if (len == bestLength && p == previousHolder) {
                holder = p;
            }
        }
        return holder != null ? holder : previousHolder;
    }
}
