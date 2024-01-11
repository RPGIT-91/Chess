package game.movegeneration.pieces;

import java.util.List;

import game.board.GameState;
import game.movegeneration.BitBoards;

/**
 * The Knight class represents the Knight chess piece and implements the PieceI interface.
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.PieceI
 * 
 * @author Ryu
 * @version 1.0
 */
public class Knight implements PieceI {
	/**
     * Bitboard representing knight attacks.
     */
	public static long knightAttacks;

	private final int pieceType = 2;
	private final int pieceColour; // 0 for white, 1 for black

	/**
     * Array representing knight moves for move generation.
     */
	private static int[] knightMoves = {-17, -15, -10, -6, 6, 10, 15, 17};

	/**
     * Constructor for the Knight class.
     *
     * @param pieceColour The color of the knight (0 for white, 1 for black).
     * @param pos         The initial position of the knight.
     */
	public Knight(int pieceColour, int pos) {
		this.pieceColour = pieceColour;

		BitBoards.add(pos, pieceType, pieceColour);
	}

	@Override
	public int getPieceType() {
		return pieceType;
	}

	@Override
	public int getPieceColour() {
		return pieceColour;
	}


	@Override
	public long generateMove(int from, boolean isWhite, GameState previousGameState) {
		long position = 1L << from;
		long possibleMoves = 0L;
		if (BitBoards.doubleCheck(isWhite)) {
			for (int move : knightMoves) {
				// Calculate the new position
				long newPosition = (position << move) | (position >>> -move);

				// Check if the new square is unoccupied or occupied by an opponent's piece
				if ((newPosition & BitBoards.allBB) == 0) {
					possibleMoves |= newPosition;
				} else {
					if ((isWhite && (newPosition & BitBoards.blackBB) != 0) || (!isWhite && (newPosition & BitBoards.whiteBB) != 0)) {
						possibleMoves |= newPosition;
					}
				}
			}
			//remove moves that are not within the knights boundaries
			possibleMoves = possibleMoves & generate5x5SquareMask(position);
			//Remove options when king in check
			long checkedMask = BitBoards.singleCheck(isWhite);
			if (checkedMask != 0) {
				possibleMoves &= checkedMask;
			}
			//Remove options when pinned
			possibleMoves &= BitBoards.checkOrthogonalPin(from, isWhite);
			possibleMoves &= BitBoards.checkDiagonalPin(from, isWhite);
			
		}
		return possibleMoves;
	}

    /**
     * Generates attacks for the knight of the same color on the board.
     * Uses BitBoards to retrieve additional Piece Information and as such is static.
     * 
     * @param isWhite Whether the knight is white.
     * @return Bitboard representing possible attacks.
     */
	public static long generateSamePieceAttacks(boolean isWhite) {
		long possibleMoves = 0L;
		knightAttacks = 0L;

		long pieceBB = (isWhite ? BitBoards.whiteKnightsBB:BitBoards.blackKnightsBB);

		List<Long> individualBBKnight = BitBoards.createIndividualBitboards(pieceBB);
		// Print the individual bitboards
		for (long bb : individualBBKnight) {
			// Knight moves are represented by relative positions
			for (int move : knightMoves) {
				// Calculate the new position
				long newPosition = (bb << move) | (bb >>> -move);

				// Create a bit mask for the new square
				long newSquareMask = 1L << (Long.numberOfTrailingZeros(newPosition));

				possibleMoves |= newSquareMask;

			}
			//remove moves that are not within the knights boundaries
			possibleMoves = possibleMoves & generate5x5SquareMask(bb);

			knightAttacks |= possibleMoves;

		}
		return knightAttacks;

	}


	/**
     * Generates a 5x5 square mask centered around the given position to limit moves within the knight's range.
     *
     * @param position The position of the knight.
     * @return Bitboard representing a 5x5 square mask.
     */
	public static long generate5x5SquareMask(long position) {
		// Create a 5x5 square around the given position
		long squareMask = 0L;

		// Iterate over rows and columns of the 5x5 square
		for (int row = -2; row <= 2; row++) {
			for (int col = -2; col <= 2; col++) {
				int newRow = Long.numberOfTrailingZeros(position) / 8 + row;
				int newCol = Long.numberOfTrailingZeros(position) % 8 + col;

				// Check if the new position is within the chessboard boundaries
				if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
					// Calculate the index of the new position in a flattened 1D representation
					int newIndex = newRow * 8 + newCol;

					// Set the corresponding bit in the mask
					squareMask |= 1L << newIndex;
				}
			}
		}

		return squareMask;
	}

	public void toggleBB(int square, boolean isWhite){
		BitBoards.knightsBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}
}
