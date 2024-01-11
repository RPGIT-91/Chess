package game.search;

/**
 * Represents a chess move from one square to another.
 * 
 * @see game.search.MoveOrdering
 * 
 * @author Ryu
 * @version 1.0
 */
public class Move {
    private int from;
    private int to;

    /**
     * Creates a new Move object with the specified source and destination squares.
     *
     * @param from The square index from which the move originates.
     * @param to   The square index to which the move is made.
     */
    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the square index from which the move originates.
     *
     * @return The square index from which the move originates.
     */
    public int getFrom() {
        return from;
    }

    /**
     * Gets the square index to which the move is made.
     *
     * @return The square index to which the move is made.
     */
    public int getTo() {
        return to;
    }
}