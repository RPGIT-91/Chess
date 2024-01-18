package game.movegeneration;

import java.util.ArrayList;

/**
 * The BitBoardHelper class provides helper methods for working with bitboards.
 * 
 * @see game.movegeneration.BitBoards
 * 
 * @author Ryu
 * @version 1.0
 */
public class BitBoardHelper {
	public static long setSquare(long bitboard, int squareIndex) {
		bitboard |= 1L << squareIndex;
		return bitboard;
	}

	public static long clearSquare(long bitboard, int squareIndex) {
		bitboard &= ~(1L << squareIndex);
		return bitboard;
	}

	public static long toggleSquares(long bitboard, int squareA, int squareB) {
		bitboard ^= (1L << squareA | 1L << squareB);
		return bitboard;
	}

	public static boolean containsSquare(long bitboard, int square) {
		return ((bitboard >> square) & 1) != 0;
	}

	
	/**
     * Counts the number of set bits in the given bitboard.
     *
     * @param bitboard The bitboard to count set bits.
     * @return The number of set bits.
     */
	public static int countSetBits(long bitboard) {
		int count = 0;

		while (bitboard != 0) {
			count += bitboard & 1;
			bitboard >>>= 1;
		}

		return count;
	}

	/**
     * Gets a list of all positions (indices of set bits) in the given bitboard.
     *
     * @param bitboard The bitboard to get positions from.
     * @return ArrayList containing all positions.
     */
	public static ArrayList<Integer> getAllPos(long bitboard) {
		ArrayList<Integer> posList = new ArrayList<>();

		while (bitboard != 0) {
			int i = Long.numberOfTrailingZeros(bitboard);
			bitboard &= (bitboard - 1);
			posList.add(i);         
		}
		return posList;
	}

}


