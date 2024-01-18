package game.search;

import game.board.Board;

/**
 * Utility class for reading values from piece square tables.
 * 
 * @see game.search.Evaluation
 * @see game.search.MaterialInfo
 * 
 * @author Ryu
 * @version 1.0
 */
class PieceSquareTable {

	/**
     * Reads the value from the piece square table for the given square and side.
     *
     * @param table    The piece square table to read from.
     * @param square   The chessboard square (0-63) for which to retrieve the value.
     * @param isWhite  Indicates whether the piece belongs to the white side.
     * @return The value from the piece square table for the specified square and side.
     */
	public static int read(int[] table, int square, boolean isWhite) {
		if (isWhite) {
			//when white read from the bottom of the table
			square = Board.toBBSquare(square);
		}
		//System.out.println("Square: " + square + "= " + table[square]+" is white: " + isWhite);

		return table[square];
	}

	public static final int[] pawns = {
			0,   0,   0,   0,   0,   0,   0,   0,
			50,  50,  50,  50,  50,  50,  50,  50,
			10,  10,  20,  30,  30,  20,  10,  10,
			5,   5,  10,  25,  25,  10,   5,   5,
			0,   0,   0,  20,  20,   0,   0,   0,
			5,  -5, -10,   0,   0, -10,  -5,   5,
			5,  10,  10, -20, -20,  10,  10,   5,
			0,   0,   0,   0,   0,   0,   0,   0
	};

	public static final int[] pawnsEnd = {
			0,   0,   0,   0,   0,   0,   0,   0,
			90,  90,  80,  70,  70,  80,  90,  90,
			50,  50,  50,  50,  50,  50,  50,  50,
			30,  30,  30,  30,  30,  30,  30,  30,
			20,  20,  20,  20,  20,  20,  20,  20,
			10,  10,  10,  10,  10,  10,  10,  10,
			10,  10,  10,  10,  10,  10,  10,  10,
			0,   0,   0,   0,   0,   0,   0,   0
	};

	public static final int[] rooks =  {
			0,   0,  0,  0,  0,  0,  0,  0,
			5,  10, 10, 10, 10, 10, 10,  5,
			-5,  0,  0,  0,  0,  0,  0, -5,
			-5,  0,  0,  0,  0,  0,  0, -5,
			-5,  0,  0,  0,  0,  0,  0, -5,
			-5,  0,  0,  0,  0,  0,  0, -5,
			-5,  0,  0,  0,  0,  0,  0, -5,
			0,  0,  0,  5,  5,  0,  0,  0
	};

	public static final int[] knights = {
			-50,-40,-30,-30,-30,-30,-40,-50,
			-40,-20,  0,  0,  0,  0,-20,-40,
			-30,  0, 10, 15, 15, 10,  0,-30,
			-30,  5, 15, 20, 20, 15,  5,-30,
			-30,  0, 15, 20, 20, 15,  0,-30,
			-30,  5, 10, 15, 15, 10,  5,-30,
			-40,-20,  0,  5,  5,  0,-20,-40,
			-50,-40,-30,-30,-30,-30,-40,-50
	};

	public static final int[] bishops =  {
			-20,-10,-10,-10,-10,-10,-10,-20,
			-10,  0,  0,  0,  0,  0,  0,-10,
			-10,  0,  5, 10, 10,  5,  0,-10,
			-10,  5,  5, 10, 10,  5,  5,-10,
			-10,  0, 10, 10, 10, 10,  0,-10,
			-10, 10, 10, 10, 10, 10, 10,-10,
			-10,  5,  0,  0,  0,  0,  5,-10,
			-20,-10,-10,-10,-10,-10,-10,-20
	};

	public static final int[] queens =  {
			-20,-10,-10, -5, -5,-10,-10,-20,
			-10,  0,  0,  0,  0,  0,  0,-10,
			-10,  0,  5,  5,  5,  5,  0,-10,
			-5,   0,  5,  5,  5,  5,  0, -5,
			-5,   0,  5,  5,  5,  5,  0, -5,
			-10,  0,  5,  5,  5,  5,  0,-10,
			-10,  0,  0,  0,  0,  0,  0,-10,
			-20,-10,-10, -5, -5,-10,-10,-20
	};

	public static final int[] kingStart = {
			-80, -70, -70, -70, -70, -70, -70, -80, 
			-60, -60, -60, -60, -60, -60, -60, -60, 
			-40, -50, -50, -60, -60, -50, -50, -40, 
			-30, -40, -40, -50, -50, -40, -40, -30, 
			-20, -30, -30, -40, -40, -30, -30, -20, 
			-10, -20, -20, -20, -20, -20, -20, -10, 
			20,  20,  -5,  -5,  -5,  -5,  20,  20, 
			20,  30,  10,   0,   0,  10,  30,  20
	};

	public static final int[] kingEnd = {
			-20, -10, -10, -10, -10, -10, -10, -20,
			-5,   0,   5,   5,   5,   5,   0,  -5,
			-10, -5,   20,  30,  30,  20,  -5, -10,
			-15, -10,  35,  45,  45,  35, -10, -15,
			-20, -15,  30,  40,  40,  30, -15, -20,
			-25, -20,  20,  25,  25,  20, -20, -25,
			-30, -25,   0,   0,   0,   0, -25, -30,
			-50, -30, -30, -30, -30, -30, -30, -50
	};
}
