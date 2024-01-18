package game.movegeneration.pieces;

import java.util.List;

import game.board.GameState;
import game.movegeneration.BitBoards;

/**
 * The Bishop class represents the Bishop chess piece and implements the PieceI interface.
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.pieces.PieceI
 * 
 * @author Ryu
 * @version 1.0
 */
public class Bishop implements PieceI {
	private final int pieceType = 3;
	private final int pieceColour; // 0 for white, 1 for black
	
	/**
     * Constructor for the Bishop class.
     *
     * @param pieceColour The color of the bishop (0 for white, 1 for black).
     * @param pos         The initial position of the bishop.
     */
	public Bishop(int pieceColour, int pos) {
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
			possibleMoves |= BitBoards.generateDiagonalSlider(position, isWhite, possibleMoves, false);
			
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
     * Generates attacks for the bishop of the same color on the board.
     * Uses BitBoards to retrieve additional Piece Information and as such is static.
     * 
     * @param isWhite Whether the bishop is white.
     * @return Bitboard representing possible attacks.
     */
	public static long generateSamePieceAttacks(boolean isWhite) {
		long possibleMoves = 0L;

		long pieceBB = (isWhite ? BitBoards.whiteBishopsBB:BitBoards.blackBishopsBB);

		List<Long> individualBBBishop = BitBoards.createIndividualBitboards(pieceBB);
		// Print the individual bitboards
		for (long bb : individualBBBishop) {
			possibleMoves |= BitBoards.generateDiagonalSlider(bb, isWhite, possibleMoves, true);
		}
		return possibleMoves;

	}

	@Override
	public void toggleBB(int square, boolean isWhite){
		BitBoards.bishopsBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}

}
