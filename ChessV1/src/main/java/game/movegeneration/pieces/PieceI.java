package game.movegeneration.pieces;

import game.board.GameState;

/**
 * The PieceI interface represents a chess piece and provides methods for interacting with it.
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.pieces.Pawn
 * @see game.movegeneration.pieces.Knight
 * @see game.movegeneration.pieces.Bishop
 * @see game.movegeneration.pieces.Rook
 * @see game.movegeneration.pieces.Queen
 * @see game.movegeneration.pieces.King
 * 
 * @author Ryu
 * @version 1.0
 */
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

	/**
     * Returns a unique identifier for each piece type.
     * 0 for empty, 1 for pawn, 2 for knight, 3 for bishop, 4 for rook, 5 for queen, 6 for king
     *
     * @return The piece type identifier.
     */
	int getPieceType();
	/**
     * Returns the color of the piece (e.g., 0 for white, 1 for black).
     *
     * @return The piece color.
     */
	int getPieceColour(); 
	
	/**
    * returns true when the selected piece is white.
    *
    * @return true if white.
    */
	default boolean isWhite() {	
		return getPieceColour() == 0; 
	}
	
	
	/**
     * Generates legal moves for the piece from the given position.
     *
     * @param from              The starting position of the piece.
     * @param isWhite           Whether the piece is white.
     * @param previousGameState The previous game state for move generation.
     * @return A bitboard representing possible moves.
     */
	long generateMove(int from, boolean isWhite, GameState previousGameState);

	//BB to see which squares are defended, and attacked. - Attack Mask
	//implemented in every Class as to calc
	//public static long generateSamePieceAttacks(boolean isWhite);

	 /**
     * Toggles the bitboard for the given square based on the piece color.
     *
     * @param from    The square to toggle.
     * @param isWhite Whether the piece is white.
     */
	void toggleBB(int from, boolean isWhite); //basically remove or add.


	/**
     * Checks if a move from one square to another is valid for the piece.
     *
     * @param from              The starting square.
     * @param to                The target square.
     * @param previousGameState The previous game state for move validation.
     * @return True if the move is valid, false otherwise.
     */
	default boolean isValidMove(int from, int to, GameState previousGameState) {
		long mask = 1L << to;

		if ((mask & generateMove(from, getPieceColour() == 0, previousGameState)) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * Adds a piece to the square-centric board and bitboards based on the provided values.
     *
     * @param pos          The position to add the piece.
     * @param pieceType    The type of the piece.
     * @param pieceColour  The color of the piece.
     * @return The newly created chess piece.
     */
	static PieceI addPiece(int pos, int pieceType, int pieceColour) {

		PieceI newPiece;

		// Create a new chess piece based on the provided values
		switch (pieceType) {
		case 1:
			newPiece = new Pawn(pieceColour, pos);
			break;
		case 2:
			newPiece = new Knight(pieceColour, pos);
			break;
		case 3:
			newPiece = new Bishop(pieceColour, pos);
			break;
		case 4:
			newPiece = new Rook(pieceColour, pos);
			break;
		case 5:
			newPiece = new Queen(pieceColour, pos);
			break;
		case 6:
			newPiece = new King(pieceColour, pos);
			break;
		default:
			newPiece = new Pawn(pieceColour, pos);
		}
		return newPiece;
	}

	// ########################### Debugging
	/**
     * Debugging
     * Prints the bitboard mask for debugging purposes.
     *
     * @param bits The bitboard mask to print.
     */
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