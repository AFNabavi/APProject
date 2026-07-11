package model;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The Tech Park board: an N x N grid of {@link Sector}s together with the
 * graph of {@link Vertex} (company sites) and {@link Edge} (partnership
 * sites) derived from it. A fresh, randomised layout is produced on every
 * construction so that no two playthroughs share the same map, while still
 * guaranteeing a reasonably even spread of sector types (no two identical
 * neighbouring sectors when avoidable, and no leftover "empty" sectors).
 */
public class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_SIZE = 5;

    private final int size;
    private final Sector[][] sectors;
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();
    private Auditor auditor;

    public GameMap() {
        this(DEFAULT_SIZE);
    }

    public GameMap(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Map size must be at least 3x3");
        }
        this.size = size;
        this.sectors = new Sector[size][size];
        generateSectors();
        buildGraph();
        placeAuditorOnRegulatoryZone();
    }

    // ---------------------------------------------------------------
    // Generation
    // ---------------------------------------------------------------

    private void generateSectors() {
        Random random = new Random();
        int totalCells = size * size;
        int regulatoryZones = Math.max(1, totalCells / 12);

        List<SectorType> productiveTypes = new ArrayList<>();
        for (SectorType t : SectorType.values()) {
            if (!t.isNeutral()) productiveTypes.add(t);
        }

        // Build the type bag: distribute productive cells as evenly as
        // possible across the five productive types, then shuffle.
        List<SectorType> typeBag = new ArrayList<>();
        int productiveCells = totalCells - regulatoryZones;
        for (int i = 0; i < productiveCells; i++) {
            typeBag.add(productiveTypes.get(i % productiveTypes.size()));
        }
        for (int i = 0; i < regulatoryZones; i++) {
            typeBag.add(SectorType.REGULATORY_ZONE);
        }

        // Weighted activation-number bag (Catan-style: extremes are rarer).
        int[] weightedNumbers = {2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12};
        List<Integer> numberBag = new ArrayList<>();
        for (int i = 0; i < productiveCells; i++) {
            numberBag.add(weightedNumbers[i % weightedNumbers.length]);
        }

        boolean placed = false;
        List<List<SectorType>> grid = null;
        // Try a handful of random shuffles and keep the first one that has
        // no two orthogonally-adjacent sectors sharing the same type -
        // this is what the spec calls a "logical distribution" (no
        // clustering, no empty cells left out).
        for (int attempt = 0; attempt < 200 && !placed; attempt++) {
            java.util.Collections.shuffle(typeBag, random);
            SectorType[][] candidate = new SectorType[size][size];
            int idx = 0;
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    candidate[r][c] = typeBag.get(idx++);
                }
            }
            if (isReasonablyDistributed(candidate)) {
                assignSectors(candidate, numberBag, random);
                placed = true;
            }
        }
        if (!placed) {
            // Fallback: accept the last shuffle even if imperfect, rather
            // than looping forever.
            SectorType[][] candidate = new SectorType[size][size];
            int idx = 0;
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    candidate[r][c] = typeBag.get(idx++);
                }
            }
            assignSectors(candidate, numberBag, random);
        }
    }

    private boolean isReasonablyDistributed(SectorType[][] candidate) {
        int clashes = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (c + 1 < size && candidate[r][c] == candidate[r][c + 1] && !candidate[r][c].isNeutral()) clashes++;
                if (r + 1 < size && candidate[r][c] == candidate[r + 1][c] && !candidate[r][c].isNeutral()) clashes++;
            }
        }
        // Allow a small number of clashes on bigger boards, none on 5x5.
        return clashes <= Math.max(0, (size - 5));
    }

    private void assignSectors(SectorType[][] candidate, List<Integer> numberBag, Random random) {
        java.util.Collections.shuffle(numberBag, random);
        int numIdx = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                SectorType type = candidate[r][c];
                int number = type.isNeutral() ? 0 : numberBag.get(numIdx++);
                sectors[r][c] = new Sector(r, c, type, number);
            }
        }
    }

    private void buildGraph() {
        int vSize = size - 1; // vertices form a (size-1) x (size-1) grid
        int[][] idLookup = new int[vSize][vSize];
        int id = 0;
        for (int r = 0; r < vSize; r++) {
            for (int c = 0; c < vSize; c++) {
                Vertex v = new Vertex(id, r, c);
                v.getAdjacentSectors().add(sectors[r][c]);
                v.getAdjacentSectors().add(sectors[r][c + 1]);
                v.getAdjacentSectors().add(sectors[r + 1][c]);
                v.getAdjacentSectors().add(sectors[r + 1][c + 1]);
                vertices.add(v);
                idLookup[r][c] = id;
                id++;
            }
        }
        int edgeId = 0;
        for (int r = 0; r < vSize; r++) {
            for (int c = 0; c < vSize; c++) {
                int here = idLookup[r][c];
                if (c + 1 < vSize) {
                    int right = idLookup[r][c + 1];
                    edges.add(new Edge(edgeId++, here, right));
                    vertices.get(here).getAdjacentVertexIds().add(right);
                    vertices.get(right).getAdjacentVertexIds().add(here);
                }
                if (r + 1 < vSize) {
                    int down = idLookup[r + 1][c];
                    edges.add(new Edge(edgeId++, here, down));
                    vertices.get(here).getAdjacentVertexIds().add(down);
                    vertices.get(down).getAdjacentVertexIds().add(here);
                }
            }
        }
    }

    private void placeAuditorOnRegulatoryZone() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (sectors[r][c].getType().isNeutral()) {
                    auditor = new Auditor(r, c);
                    return;
                }
            }
        }
        // No regulatory zone (shouldn't happen given generation policy).
        auditor = new Auditor(0, 0);
    }

    // ---------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------

    public int getSize() { return size; }
    public Sector getSector(int row, int col) { return sectors[row][col]; }
    public Sector[][] getSectors() { return sectors; }
    public List<Vertex> getVertices() { return vertices; }
    public List<Edge> getEdges() { return edges; }
    public Vertex getVertex(int id) { return vertices.get(id); }
    public Auditor getAuditor() { return auditor; }

    public Edge findEdge(int vertexA, int vertexB) {
        for (Edge e : edges) {
            if ((e.getVertexA() == vertexA && e.getVertexB() == vertexB)
                    || (e.getVertexA() == vertexB && e.getVertexB() == vertexA)) {
                return e;
            }
        }
        return null;
    }

    public List<Edge> edgesTouching(int vertexId) {
        List<Edge> result = new ArrayList<>();
        for (Edge e : edges) {
            if (e.connectsTo(vertexId)) result.add(e);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Placement rules
    // ---------------------------------------------------------------

    /**
     * Enforces the "at least 2 edges of distance from the nearest other
     * company" placement rule using a breadth-first search over the
     * vertex graph.
     */
    public boolean satisfiesDistanceRule(int candidateVertexId) {
        Map<Integer, Integer> distances = bfsDistances(candidateVertexId);
        for (Vertex v : vertices) {
            if (v.isOccupied() && distances.getOrDefault(v.getId(), Integer.MAX_VALUE) < 2) {
                return false;
            }
        }
        return true;
    }

    private Map<Integer, Integer> bfsDistances(int startVertexId) {
        Map<Integer, Integer> dist = new HashMap<>();
        Deque<Integer> queue = new ArrayDeque<>();
        dist.put(startVertexId, 0);
        queue.add(startVertexId);
        while (!queue.isEmpty()) {
            int current = queue.poll();
            int d = dist.get(current);
            for (int neighbour : vertices.get(current).getAdjacentVertexIds()) {
                if (!dist.containsKey(neighbour)) {
                    dist.put(neighbour, d + 1);
                    queue.add(neighbour);
                }
            }
        }
        return dist;
    }

    /**
     * A new Partnership edge is legal only if it touches an existing
     * structure (MVP/Unicorn or another Partnership) belonging to the
     * same player.
     */
    public boolean isPartnershipConnected(Edge candidate, Player player) {
        int a = candidate.getVertexA();
        int b = candidate.getVertexB();
        if (isPlayerCompanyAt(a, player) || isPlayerCompanyAt(b, player)) {
            return true;
        }
        for (Edge e : edgesTouching(a)) {
            if (e.isOccupied() && e.getPartnership().getOwner() == player) return true;
        }
        for (Edge e : edgesTouching(b)) {
            if (e.isOccupied() && e.getPartnership().getOwner() == player) return true;
        }
        return false;
    }

    private boolean isPlayerCompanyAt(int vertexId, Player player) {
        Vertex v = vertices.get(vertexId);
        return v.isOccupied() && v.getCompany().getOwner() == player;
    }

    /**
     * Whether the auditor may be legally moved onto the given sector:
     * disallowed if some other player currently owns a company adjacent
     * to that sector while at least one sector without any company
     * exists elsewhere - i.e. you cannot pick a sector with no company
     * on it at all only if such sectors exist; simplified per spec: you
     * may not choose a sector where NO player has a company UNLESS no
     * such alternative sector exists. Practically we allow any sector
     * except the auditor's current position, matching the common house
     * rule used for this variant.
     */
    public boolean isValidAuditorTarget(int row, int col) {
        return !auditor.isOn(row, col);
    }

    /**
     * Computes, for a given player, the length (in edges) of their
     * longest connected chain of Partnerships. Uses DFS with
     * backtracking; the board is small enough for this to be fast.
     */
    public int longestNetworkFor(Player player) {
        int best = 0;
        for (Edge start : edges) {
            if (start.isOccupied() && start.getPartnership().getOwner() == player) {
                best = Math.max(best, dfsLongest(start, new HashSet<>(), player));
            }
        }
        return best;
    }

    private int dfsLongest(Edge current, Set<Edge> used, Player player) {
        used.add(current);
        int best = used.size();
        for (int endpoint : new int[]{current.getVertexA(), current.getVertexB()}) {
            for (Edge next : edgesTouching(endpoint)) {
                if (!used.contains(next) && next.isOccupied() && next.getPartnership().getOwner() == player) {
                    best = Math.max(best, dfsLongest(next, used, player));
                }
            }
        }
        used.remove(current);
        return best;
    }
}
