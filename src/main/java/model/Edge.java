package model;

// import model.partnership.Partnership;

public class Edge {

    private int id;

    private Vertex first;

    private Vertex second;

    private Partnership partnership;

    public Edge(int id, Vertex first, Vertex second) {
        this.id = id;
        this.first = first;
        this.second = second;
    }

    public int getId() {
        return id;
    }

    public Vertex getFirst() {
        return first;
    }

    public Vertex getSecond() {
        return second;
    }

    public Partnership getPartnership() {
        return partnership;
    }

    public void setPartnership(Partnership partnership) {
        this.partnership = partnership;
    }
}