/**
 * The BitBoardHelper class provides helper methods for working with bitboards.
 * 
 * @author Ryu
 * @version 1.0
 */

package game.movegeneration;

import java.util.ArrayList;


public class BitBoardHelper {
	/**
	 * Bitmask for file A.
	 */
	public static final long fileA = 0x101010101010101L;
	/**
     * Bitmask for rank 1.
     */
	public static final long rank1 = 0b11111111L;
	/**
     * Bitmask for rank 2.
     */
	public static final long rank2 = rank1 << 8;
	/**
     * Bitmask for rank 3.
     */
	public static final long rank3 = rank2 << 8;
	/**
     * Bitmask for rank 4.
     */
	public static final long rank4 = rank3 << 8;
	/**
	 * Bitmask for rank 5.
	 */
	public static final long rank5 = rank4 << 8;
	/**
	 * Bitmask for rank 6.
	 */
	public static final long rank6 = rank5 << 8;
	/**
	 * Bitmask for rank 7.
	 */
	public static final long rank7 = rank6 << 8;
	/**
	 * Bitmask for rank 8.
	 */
	public static final long Rank8 = rank7 << 8;
	/**
     * Bitmask for not A file.
     */
	public static final long notAFile = ~fileA;
	/**
     * Bitmask for not H file.
     */
	public static final long notHFile = ~(fileA << 7);

	/**
     * Gets the index of the least significant set bit in the given 64-bit value and clears the bit to zero.
     *
     * @param b The array containing the 64-bit value.
     * @return The index of the least significant set bit.
     */
	public static int popLSB(long[] b) {
		int i = Long.numberOfTrailingZeros(b[0]);
		b[0] &= (b[0] - 1);
		return i;
	}

	public static long setSquare(long bitboard, int squareIndex) {
		bitboard |= 1L << squareIndex;
		return bitboard;
	}

	public static long clearSquare(long bitboard, int squareIndex) {
		bitboard &= ~(1L << squareIndex);
		return bitboard;
	}

	public static long toggleSquare(long bitboard, int squareIndex) {
		bitboard ^= 1L << squareIndex;
		return bitboard;
	}

	public static long toggleSquares(long bitboard, int squareA, int squareB) {
		bitboard ^= (1L << squareA | 1L << squareB);
		return bitboard;
	}

	public static boolean containsSquare(long bitboard, int square) {
		return ((bitboard >> square) & 1) != 0;
	}

	public static long shift(long bitboard, int numSquaresToShift) {
		if (numSquaresToShift > 0) {
			return bitboard << numSquaresToShift;
		} else {
			return bitboard >>> -numSquaresToShift;
		}
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


