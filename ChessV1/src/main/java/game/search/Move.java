package game.search;

public class Move {
    private int from;
    private int to;
    // You might include additional information such as promotion piece, capture flag, etc.

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    // You can include other methods or properties as needed
}