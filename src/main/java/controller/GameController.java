package controller;

import exception.InsufficientResourcesException;
import exception.InvalidPlacementException;
import model.*;
import util.Constants;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The engine that owns all game rules described in the specification:
 * setup, the turn cycle (roll -> production/crisis -> actions -> end),
 * construction rules, the dynamic market and victory conditions. The
 * view layer never mutates {@link GameState} directly; it always goes
 * through this controller so invariants stay consistent.
 */
public class GameController {

    private GameState state;
    private TurnPhase phase = TurnPhase.SETUP;
    private final List<GameListener> listeners = new ArrayList<>();

    // Setup-phase bookkeeping
    private final List<Integer> setupOrder = new ArrayList<>();
    private int setupOrderIndex = 0;
    private boolean lastSetupPlayerExtraTurnPending = false;

    public GameController(List<String> playerNames, int mapSize) {
        if (playerNames.size() < Constants.MIN_PLAYERS || playerNames.size() > Constants.MAX_PLAYERS) {
            throw new IllegalArgumentException("Player count must be between "
                    + Constants.MIN_PLAYERS + " and " + Constants.MAX_PLAYERS);
        }
        this.state = new GameState();
        state.setMap(new GameMap(mapSize));
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(i, playerNames.get(i), Constants.DEFAULT_PLAYER_COLORS[i]));
        }
        state.setPlayers(players);
        for (int i = 0; i < players.size(); i++) setupOrder.add(i);
        state.setCurrentPlayerIndex(setupOrder.get(0));
        state.log(GameEvent.Type.INFO, "بازی جدید ایجاد شد با " + players.size() + " بازیکن.");
    }

    public GameState getState() { return state; }
    public TurnPhase getPhase() { return phase; }

    /**
     * Replaces the current game state with one restored from disk
     * (used by the Load feature). Recomputes the turn-phase machine so
     * the controller stays consistent with the restored data.
     */
    public void loadState(GameState loaded) {
        this.state = loaded;
        this.setupOrderIndex = 0;
        this.setupOrder.clear();
        for (int i = 0; i < loaded.getPlayers().size(); i++) setupOrder.add(i);
        this.phase = loaded.isSetupPhase() ? TurnPhase.SETUP : TurnPhase.AWAITING_ROLL;
        fireChanged();
    }

    public void addListener(GameListener listener) { listeners.add(listener); }
    private void fireChanged() { for (GameListener l : listeners) l.onStateChanged(); }
    private void log(GameEvent.Type type, String message) {
        state.log(type, message);
        for (GameListener l : listeners) l.onLog(message);
    }

    // =================================================================
    // SETUP PHASE
    // =================================================================

    /** Optional: must be called (if at all) before initial placement starts. */
    public void chooseRole(Player player, FounderRole role) {
        player.setRole(role);
        if (role == FounderRole.VC_FUNDED) {
            player.addResource(Resource.CAPITAL, 2);
        }
        log(GameEvent.Type.ROLE, player.getName() + " نقش " + role.getEnglishName() + " را انتخاب کرد.");
        fireChanged();
    }

    /**
     * Places the free initial MVP + connected Partnership for the
     * current setup turn (forward round then reverse round, per rules).
     * No cost is charged during setup.
     */
    public void placeInitialCompany(int vertexId, int edgeId) throws InvalidPlacementException {
        Player current = state.getCurrentPlayer();
        Vertex vertex = state.getMap().getVertex(vertexId);
        if (vertex.isOccupied()) {
            throw new InvalidPlacementException("این گره قبلا اشغال شده است.");
        }
        if (!state.getMap().satisfiesDistanceRule(vertexId)) {
            throw new InvalidPlacementException("این گره به شرکت دیگری بسیار نزدیک است (حداقل ۲ یال فاصله لازم است).");
        }
        Edge edge = findEdgeById(edgeId);
        if (edge == null || !edge.connectsTo(vertexId)) {
            throw new InvalidPlacementException("یال انتخابی به گره متصل نیست.");
        }
        if (edge.isOccupied()) {
            throw new InvalidPlacementException("این یال قبلا اشغال شده است.");
        }

        MVP mvp = new MVP(current);
        vertex.setCompany(mvp);
        current.addStructure(mvp);

        Partnership partnership = new Partnership(current);
        edge.setPartnership(partnership);
        current.addStructure(partnership);

        log(GameEvent.Type.BUILD, current.getName() + " یک MVP و یک Partnership در فاز راه‌اندازی ساخت.");

        if (state.getSetupRound() == 2) {
            // Award starting resources from sectors adjacent to the SECOND MVP.
            for (Sector s : vertex.getAdjacentSectors()) {
                if (!s.getType().isNeutral()) {
                    current.addResource(s.getType().getProducedResource(), 1);
                }
            }
            log(GameEvent.Type.PRODUCTION, current.getName() + " منابع اولیه خود را دریافت کرد.");
        }

        advanceSetupTurn();
        fireChanged();
    }

    private Edge findEdgeById(int edgeId) {
        for (Edge e : state.getMap().getEdges()) {
            if (e.getId() == edgeId) return e;
        }
        return null;
    }

    private void advanceSetupTurn() {
        setupOrderIndex++;
        if (setupOrderIndex < setupOrder.size()) {
            state.setCurrentPlayerIndex(setupOrder.get(setupOrderIndex));
            return;
        }
        // Finished a full pass over all players.
        if (state.getSetupRound() == 1) {
            state.setSetupRound(2);
            List<Integer> reversed = new ArrayList<>(setupOrder);
            java.util.Collections.reverse(reversed);
            setupOrder.clear();
            setupOrder.addAll(reversed);
            setupOrderIndex = 0;
            state.setCurrentPlayerIndex(setupOrder.get(0));
        } else {
            // Setup fully complete: real turn order resumes with player 0.
            state.setSetupPhase(false);
            state.setCurrentPlayerIndex(0);
            phase = TurnPhase.AWAITING_ROLL;
            log(GameEvent.Type.INFO, "فاز راه‌اندازی پایان یافت. بازی اصلی آغاز می‌شود.");
        }
    }

    // =================================================================
    // TURN CYCLE
    // =================================================================

    /** Step 1 of a turn: roll the dice and resolve production or crisis. */
    public int rollDice() {
        if (phase != TurnPhase.AWAITING_ROLL) {
            throw new IllegalStateException("در این مرحله نمی‌توان تاس انداخت.");
        }
        int sum = state.getDice().roll();
        log(GameEvent.Type.DICE, state.getCurrentPlayer().getName() + " تاس انداخت: " + sum);

        if (sum == Constants.CRISIS_DICE_SUM) {
            resolveCrisisTaxOnly();
            log(GameEvent.Type.CRISIS, "بحران قانونی رخ داد! (مجموع تاس = ۷)");
        } else {
            resolveProduction(sum);
        }
        phase = TurnPhase.ACTIONS;
        fireChanged();
        return sum;
    }

    private void resolveProduction(int diceSum) {
        GameMap map = state.getMap();
        for (Sector[] row : map.getSectors()) {
            for (Sector sector : row) {
                if (!sector.isActivatedBy(diceSum)) continue;
                if (map.getAuditor().isOn(sector.getRow(), sector.getCol())) continue; // blocked
                Resource produced = sector.getType().getProducedResource();
                for (Vertex v : map.getVertices()) {
                    if (!v.getAdjacentSectors().contains(sector)) continue;
                    if (!v.isOccupied()) continue;
                    v.getCompany().produce(produced);
                }
            }
        }
        log(GameEvent.Type.PRODUCTION, "منابع بر اساس عدد " + diceSum + " توزیع شد.");
    }

    /**
     * Applies only the "return half your cards" taxation part of the
     * crisis automatically (each player over the limit returns their
     * highest-count resources first, a reasonable deterministic default);
     * the auditor repositioning is a separate explicit action the
     * rolling player must take via {@link #moveAuditor(int, int)}.
     */
    private void resolveCrisisTaxOnly() {
        for (Player p : state.getPlayers()) {
            int limit = p.getCrisisCardLimit();
            int total = p.getTotalResourceCount();
            if (total > limit) {
                int toReturn = total / 2; // rounded down
                autoReturnResources(p, toReturn);
                log(GameEvent.Type.CRISIS, p.getName() + " به دلیل مالیات " + toReturn + " کارت بازگرداند.");
            }
        }
    }

    private void autoReturnResources(Player p, int amountToReturn) {
        int remaining = amountToReturn;
        while (remaining > 0) {
            Resource richest = null;
            int max = -1;
            for (Resource r : Resource.values()) {
                if (p.getResourceCount(r) > max) {
                    max = p.getResourceCount(r);
                    richest = r;
                }
            }
            if (richest == null || max <= 0) break;
            p.removeResource(richest, 1);
            remaining--;
        }
    }

    /** The player who rolled the 7 must reposition the auditor. */
    public void moveAuditor(int row, int col) throws InvalidPlacementException {
        GameMap map = state.getMap();
        if (!map.isValidAuditorTarget(row, col)) {
            throw new InvalidPlacementException("بازرس هم اکنون روی این سکتور قرار دارد.");
        }
        map.getAuditor().moveTo(row, col);
        log(GameEvent.Type.CRISIS, "بازرس قانونی به سکتور (" + row + "," + col + ") منتقل شد.");
        fireChanged();
    }

    // ---- Actions (Step 2, any order, any number of times) -----------

    public void buildPartnership(int edgeId) throws InvalidPlacementException, InsufficientResourcesException {
        requireActionsPhase();
        Player current = state.getCurrentPlayer();
        Edge edge = findEdgeById(edgeId);
        if (edge == null) throw new InvalidPlacementException("یال نامعتبر است.");
        if (edge.isOccupied()) throw new InvalidPlacementException("این یال قبلا اشغال شده است.");
        if (!state.getMap().isPartnershipConnected(edge, current)) {
            throw new InvalidPlacementException("Partnership باید به شبکه فعلی شما متصل باشد.");
        }
        Partnership partnership = new Partnership(current);
        chargeOrThrow(current, partnership.getCost());
        edge.setPartnership(partnership);
        current.addStructure(partnership);
        log(GameEvent.Type.BUILD, current.getName() + " یک Partnership ساخت.");
        refreshLongestNetwork();
        fireChanged();
    }

    public void buildMVP(int vertexId) throws InvalidPlacementException, InsufficientResourcesException {
        requireActionsPhase();
        Player current = state.getCurrentPlayer();
        Vertex vertex = state.getMap().getVertex(vertexId);
        if (vertex.isOccupied()) throw new InvalidPlacementException("این گره قبلا اشغال شده است.");
        if (!state.getMap().satisfiesDistanceRule(vertexId)) {
            throw new InvalidPlacementException("این گره به شرکت دیگری بسیار نزدیک است.");
        }
        boolean hasNetworkAccess = vertexReachableViaOwnPartnerships(vertexId, current);
        if (!hasNetworkAccess) {
            throw new InvalidPlacementException("برای ساخت MVP در این نقطه باید مسیر Partnership متصلی داشته باشید.");
        }
        MVP mvp = new MVP(current);
        chargeOrThrow(current, mvp.getCost());
        vertex.setCompany(mvp);
        current.addStructure(mvp);
        log(GameEvent.Type.BUILD, current.getName() + " یک MVP جدید ساخت.");
        fireChanged();
    }

    private boolean vertexReachableViaOwnPartnerships(int vertexId, Player player) {
        for (Edge e : state.getMap().edgesTouching(vertexId)) {
            if (e.isOccupied() && e.getPartnership().getOwner() == player) return true;
        }
        return false;
    }

    public void upgradeToUnicorn(int vertexId) throws InvalidPlacementException, InsufficientResourcesException {
        requireActionsPhase();
        Player current = state.getCurrentPlayer();
        Vertex vertex = state.getMap().getVertex(vertexId);
        if (!vertex.isOccupied() || !(vertex.getCompany() instanceof MVP) || vertex.getCompany().getOwner() != current) {
            throw new InvalidPlacementException("فقط می‌توانید MVP خودتان را ارتقا دهید.");
        }
        boolean techGuru = current.getRole() == FounderRole.TECH_GURU;
        Map<Resource, Integer> cost = Unicorn.getUpgradeCost(techGuru);
        chargeOrThrow(current, cost);
        current.getStructures().remove(vertex.getCompany());
        Unicorn unicorn = new Unicorn(current);
        vertex.setCompany(unicorn);
        current.addStructure(unicorn);
        log(GameEvent.Type.BUILD, current.getName() + " MVP خود را به Unicorn ارتقا داد.");
        fireChanged();
    }

    /** Convert surplus resources into a desired resource at the market rate. */
    public void tradeWithMarket(Resource give, Resource receive, int units) throws InsufficientResourcesException {
        requireActionsPhase();
        Player current = state.getCurrentPlayer();
        int rate = current.getRole() == FounderRole.HACKER_CEO
                ? Market.HACKER_CEO_TRADE_RATE
                : Market.STANDARD_TRADE_RATE;
        int cost = rate * units;
        if (current.getResourceCount(give) < cost) {
            throw new InsufficientResourcesException("برای این معامله " + cost + " واحد " + give.getPersianName() + " لازم است.");
        }
        current.removeResource(give, cost);
        current.addResource(receive, units);
        state.getMarket().recordPurchase(receive);
        log(GameEvent.Type.MARKET, current.getName() + " " + cost + " " + give.getPersianName()
                + " را با " + units + " " + receive.getPersianName() + " معامله کرد.");
        fireChanged();
    }

    /** Buys 1 unit of {@code target} from the market for its current Capital price. */
    public void buyFromMarket(Resource target) throws InsufficientResourcesException {
        requireActionsPhase();
        Player current = state.getCurrentPlayer();
        int price = state.getMarket().getPriceInCapital(target);
        if (current.getResourceCount(Resource.CAPITAL) < price) {
            throw new InsufficientResourcesException("برای خرید " + target.getPersianName() + " به " + price + " سرمایه نیاز دارید.");
        }
        current.removeResource(Resource.CAPITAL, price);
        current.addResource(target, 1);
        state.getMarket().recordPurchase(target);
        log(GameEvent.Type.MARKET, current.getName() + " ۱ واحد " + target.getPersianName() + " را با " + price + " سرمایه خرید.");
        fireChanged();
    }

    /** Direct player-to-player trade; both sides must agree out of game and call this once. */
    public void tradeWithPlayer(Player a, Map<Resource, Integer> aGives, Player b, Map<Resource, Integer> bGives)
            throws InsufficientResourcesException {
        if (!a.canAfford(aGives)) throw new InsufficientResourcesException(a.getName() + " منابع کافی ندارد.");
        if (!b.canAfford(bGives)) throw new InsufficientResourcesException(b.getName() + " منابع کافی ندارد.");
        a.pay(aGives);
        b.pay(bGives);
        aGives.forEach(b::addResource);
        bGives.forEach(a::addResource);
        log(GameEvent.Type.TRADE, a.getName() + " و " + b.getName() + " با یکدیگر معامله کردند.");
        fireChanged();
    }

    private void chargeOrThrow(Player player, Map<Resource, Integer> cost) throws InsufficientResourcesException {
        if (!player.canAfford(cost)) {
            String need = cost.entrySet().stream()
                    .map(e -> e.getValue() + " " + e.getKey().getPersianName())
                    .collect(Collectors.joining("، "));
            throw new InsufficientResourcesException("منابع کافی نیست. لازم است: " + need);
        }
        player.pay(cost);
    }

    private void requireActionsPhase() {
        if (phase != TurnPhase.ACTIONS) {
            throw new IllegalStateException("ابتدا باید تاس بیندازید.");
        }
    }

    // ---- Step 3: end of turn ----------------------------------------

    public void endTurn() {
        refreshLongestNetwork();
        Player current = state.getCurrentPlayer();
        int points = current.getVictoryPoints(current == state.getLongestNetworkHolder());
        if (points >= Constants.WINNING_SCORE) {
            state.setWinner(current);
            phase = TurnPhase.GAME_OVER;
            log(GameEvent.Type.WIN, current.getName() + " با " + points + " امتیاز برنده بازی شد!");
            fireChanged();
            return;
        }
        decayUntouchedMarketPrices();
        state.nextPlayer();
        phase = TurnPhase.AWAITING_ROLL;
        log(GameEvent.Type.INFO, "نوبت به " + state.getCurrentPlayer().getName() + " رسید.");
        fireChanged();
    }

    private void decayUntouchedMarketPrices() {
        // Every resource not purchased this turn edges toward the floor
        // price; recordPurchase() already reset the counter for touched
        // resources, so calling tick on everything here is safe (touched
        // resources were reset to 0 rounds-since-purchase already, so this
        // simply increments the ones that were not touched, matching the
        // "3 consecutive untouched rounds" rule).
        for (Resource r : Resource.values()) {
            state.getMarket().tickUnpurchased(r);
        }
    }

    private void refreshLongestNetwork() {
        Player holder = state.computeLongestNetworkHolder(state.getLongestNetworkHolder());
        state.setLongestNetworkHolder(holder);
    }

    public int getVictoryPoints(Player p) {
        return p.getVictoryPoints(p == state.getLongestNetworkHolder());
    }
}
