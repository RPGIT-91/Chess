package game.movegeneration.pieces;

import java.util.List;

import game.board.Board;
import game.board.GameState;
import game.movegeneration.BitBoards;

/**
 * The Queen class represents the Queen chess piece and implements the PieceI interface.
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.pieces.PieceI
 * 
 * @author Ryu
 * @version 1.0
 */
public class Queen implements PieceI {
	private final int pieceType = 5;
	private final int pieceColour; // 0 for white, 1 for black

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

		System.out.println("Starting debug");
		if (BitBoards.doubleCheck(isWhite)) {
			
			possibleMoves |= BitBoards.generateDiagonalSlider(position, isWhite, possibleMoves, false);
			possibleMoves |= BitBoards.generateOrthogonalSlider(position, isWhite, possibleMoves, false);
			
			Board.printBitBoard(possibleMoves, false);
			
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
			possibleMoves |= BitBoards.generateDiagonalSlider(bb, isWhite, possibleMoves, true);
			possibleMoves |= BitBoards.generateOrthogonalSlider(bb, isWhite, possibleMoves, true);
			
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
