package game.movegeneration.pieces;

import java.util.List;

import game.board.GameState;
import game.movegeneration.BitBoards;

public class Rook implements PieceI {
	public static long rookAttacks;

	private final int pieceType = 4;
	private final int pieceColour; // 0 for white, 1 for black
	private static int[] rookMoves = {-8, -1, 1, 8};

	//Constructor
	public Rook(int pieceColour, int pos) {
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
			//Remove options when pinned
			possibleMoves &= BitBoards.checkPin(from, isWhite);
			
			//Remove options when king in check
			long checkedMask = BitBoards.singleCheck(isWhite);
			if (checkedMask != 0) {
				possibleMoves &= checkedMask;
			}
			
		}
		return possibleMoves;
	}


	public static long generateSamePieceAttacks(boolean isWhite) {
		long possibleMoves = 0L;
		long pieceBB = (isWhite ? BitBoards.whiteRooksBB : BitBoards.blackRooksBB);

		List<Long> individualBBRook = PieceI.createIndividualBitboards(pieceBB);
		// Print the individual bitboards
		for (long bb : individualBBRook) {
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
				rookAttacks |= possibleMoves;
			}
		}
		return possibleMoves;

	}
	public void toggleBB(int square, boolean isWhite){
		BitBoards.rooksBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}
}
