/**
 * The Queen class represents the Queen chess piece and implements the PieceI interface.
 * 
 * @author Ryu
 * @version 1.0
 */

package game.movegeneration.pieces;

import java.util.List;

import game.board.GameState;
import game.movegeneration.BitBoards;

public class Queen implements PieceI {
	/**
	 * Bitboard representing queen attacks.
	 */
	public static long queenAttacks;

	private final int pieceType = 5;
	private final int pieceColour; // 0 for white, 1 for black

	/**
	 * Array representing bishop moves for move generation.
	 */
	static final int[] bishopMoves = {-9, -7, 7, 9};
	/**
	 * Array representing rook moves for move generation.
	 */
	static final int[] rookMoves = {-8, -1, 1, 8};


	/**
	 * Constructor for the Queen class.
	 *
	 * @param pieceColour The color of the queen (0 for white, 1 for black).
	 * @param pos         The initial position of the queen.
	 */
	public Queen(int pieceColour, int pos) {
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
			for (int move : bishopMoves) {
				long newPosition = position;

				// Generate diagonal moves
				while (true) {	        	
					int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

					newPosition = (newPosition << move) | (newPosition >>> -move);

					// Check if the new position is outside the board or on a different diagonal
					int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

					if (Math.abs(newRank - oldRank) == 7 | Math.abs(newRank-oldRank) == 0 | Math.abs(newFile - oldFile) == 7) {
						break;
					}
					// Check if the new square is unoccupied or occupied by an opponent's piece
					if ((newPosition & BitBoards.allBB) == 0) {
						possibleMoves |= newPosition;
					} else {
						// If the square is occupied by an opponent's piece, include the capture move and stop sliding
						if ((isWhite && (newPosition & BitBoards.blackBB) != 0) || (!isWhite && (newPosition & BitBoards.whiteBB) != 0)) {
							possibleMoves |= newPosition;
						}
						break;
					}
				}
			}
			for (int move : rookMoves) {
				long newPosition = position;

				// Generate rook moves
				while (true) {
					int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

					newPosition = (newPosition << move | newPosition >>> -move);

					// Check if the new position is outside the board or on a different rank/file
					int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

					if (Math.abs(newRank - oldRank) == 7 | Math.abs(newFile - oldFile) == 7) {
						break;
					}

					// Check if the new square is unoccupied or occupied by an opponent's piece
					if ((newPosition & BitBoards.allBB) == 0) {
						possibleMoves |= newPosition;
					} else {
						// If the square is occupied by an opponent's piece, include the capture move and stop sliding
						if ((isWhite && (newPosition & BitBoards.blackBB) != 0) || (!isWhite && (newPosition & BitBoards.whiteBB) != 0)) {
							possibleMoves |= newPosition;
						}
						break;
					}
				}
			}
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
	 * Generates attacks for the queen of the same color on the board.
	 * Uses BitBoards to retrieve additional Piece Information and as such is static
	 *
	 * @param isWhite Whether the queen is white.
	 * @return Bitboard representing possible attacks.
	 */
	public static long generateSamePieceAttacks(boolean isWhite) {
		long possibleMoves = 0L;

		long pieceBB = (isWhite ? BitBoards.whiteQueensBB:BitBoards.blackQueensBB);


		List<Long> individualBBQueen = BitBoards.createIndividualBitboards(pieceBB);
		// Print the individual bitboards
		for (long bb : individualBBQueen) {
			for (int move : bishopMoves) {
				long newPosition = bb;

				// Generate diagonal moves
				while (true) {	        	
					int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

					newPosition = (newPosition << move) | (newPosition >>> -move);




					// Check if the new position is outside the board or on a different diagonal
					int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

					if (Math.abs(newRank - oldRank) == 7 | Math.abs(newRank-oldRank) == 0 | Math.abs(newFile - oldFile) == 7) {
						break;
					}

					// Check if the new square is unoccupied or occupied by an opponent's piece
					if ((newPosition & BitBoards.allBB) == 0) {
						possibleMoves |= newPosition;
					} else {
						//stop sliding
						possibleMoves |= newPosition;
						break;
					}
				}
			}
			queenAttacks |= possibleMoves;

			for (int move : rookMoves) {
				long newPosition = bb;

				// Generate rook moves
				while (true) {
					int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

					newPosition = (newPosition << move | newPosition >>> -move);

					// Check if the new position is outside the board or on a different rank/file
					int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
					int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

					if (Math.abs(newRank - oldRank) == 7 | Math.abs(newFile - oldFile) == 7) {
						break;
					}
					// Check if the new square is unoccupied or occupied by an opponent's piece
					if ((newPosition & BitBoards.allBB) == 0) {
						possibleMoves |= newPosition;
					} else {
						//stop sliding
						possibleMoves |= newPosition;
						break;
					}
				}
				queenAttacks |= possibleMoves;
			}
		}

		return possibleMoves;

	}

	@Override
	public void toggleBB(int square, boolean isWhite){
		BitBoards.queensBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}

}
