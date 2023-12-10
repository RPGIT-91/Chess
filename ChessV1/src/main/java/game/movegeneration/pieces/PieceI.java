package game.movegeneration.pieces;

import java.util.ArrayList;
import java.util.List;

import game.board.GameState;

public interface PieceI {	
	public static final long fileA = 0x101010101010101L;
	public static final long notAFile = ~fileA;
	public static final long notHFile = ~(fileA << 7);

	public static final long rank1 = 0b11111111L;
	public static final long rank2 = rank1 << 8;
	public static final long rank3 = rank2 << 8;
	public static final long rank4 = rank3 << 8;
	public static final long rank5 = rank4 << 8;
	public static final long rank6 = rank5 << 8;
	public static final long rank7 = rank6 << 8;
	public static final long Rank8 = rank7 << 8;

	int getPieceType(); // Return a unique identifier for each piece type
	int getPieceColour(); // Return the color of the piece (e.g., 0 for white, 1 for black)
	
	long generateMove(int from, boolean isWhite, GameState previousGameState); //generate Pseudolegal Moves.
	
	//BB to see which squares are defended, and attacked. - Attack Mask
	//implemented in every Class as to calc
	//public static long generateSamePieceAttacks(boolean isWhite);
	
	void toggleBB(int from, boolean isWhite); //basically remove or add.
	
	
	default boolean isWhite() {	
		return getPieceColour() == 0; 
	}
	
	default boolean isValidMove(int from, int to, GameState previousGameState) {
		long mask = 1L << to;
		
		if ((mask & generateMove(from, getPieceColour() == 0, previousGameState)) != 0) {
			return true;
		} else {
			return false;
		}
	}


	public static List<Long> createIndividualBitboards(long bitboard) {
		List<Long> individualBitboards = new ArrayList<>();

		long remainingBits = bitboard;
		while (remainingBits != 0) {
			long leastSignificantBit = remainingBits & -remainingBits;
			individualBitboards.add(leastSignificantBit);
			remainingBits ^= leastSignificantBit;  // Clear the least significant bit
		}

		return individualBitboards;
	}
	
	//Debugging
	default void printMask(long bits) {
		for (int rank = 7; rank >= 0; rank--) {
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + file;
				long mask = 1L << square;

				if ((bits & mask) != 0) {
					System.out.print("M ");
				} else {
					System.out.print(". ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

}