package game.movegeneration;

import java.util.ArrayList;

// Methods for interacting
 public class BitBoardHelper {
	public static final long fileA = 0x101010101010101L;

	public static final long rank1 = 0b11111111L;
	public static final long rank2 = rank1 << 8;
	public static final long rank3 = rank2 << 8;
	public static final long rank4 = rank3 << 8;
	public static final long rank5 = rank4 << 8;
	public static final long rank6 = rank5 << 8;
	public static final long rank7 = rank6 << 8;
	public static final long Rank8 = rank7 << 8;

	public static final long notAFile = ~fileA;
	public static final long notHFile = ~(fileA << 7);

	// Get index of least significant set bit in given 64bit value. Also clears the bit to zero.
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
	
	public static int countSetBits(long bitboard) {
        int count = 0;

        while (bitboard != 0) {
            count += bitboard & 1;
            bitboard >>>= 1;
        }

        return count;
    }
	
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


