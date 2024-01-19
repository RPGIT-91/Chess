package game.movegeneration.pieces;

import game.board.GameState;
import game.movegeneration.BitBoards;

/**
 * The King class represents the King chess piece and implements the PieceI interface.
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.pieces.PieceI
 * 
 * @author Ryu
 * @version 1.0
 */
public class King implements PieceI {
	/**
     * Array representing king moves for move generation.
     */
	private static int[] kingMoves = {-9, -8, -7, -1, 1, 7, 8, 9};

	private final int pieceType = 6;
	private final int pieceColour; // 0 for white, 1 for black

	/**
     * Constructor for the King class.
     *
     * @param pieceColour The color of the king (0 for white, 1 for black).
     * @param pos         The initial position of the king.
     */
	public King(int pieceColour, int pos) {
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
		long attackMask = 0L;


		//move into check
		attackMask = (!isWhite ? BitBoards.whiteAM : BitBoards.blackAM);
		
		for (int move : kingMoves) {
			// Calculate the new position
			long newPosition = (position << move) | (position >>> -move);

			int oldRank = Long.numberOfTrailingZeros(position) / 8;
			int oldFile = Long.numberOfTrailingZeros(position) % 8;
			int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
			int newFile = Long.numberOfTrailingZeros(newPosition) % 8;            

			//restrict capturing at edge of board
			if (Math.abs(newRank - oldRank) <= 1 &  Math.abs(newFile - oldFile) <= 1) {
				// Create a bit mask for the new square
				long newSquareMask = 1L << (Long.numberOfTrailingZeros(newPosition));

				if ((newSquareMask & (BitBoards.allBB | attackMask)) == 0 ){
					//Check if unoccupied and not attacked
					possibleMoves |= newSquareMask;
				}

				if ((newSquareMask & attackMask) == 0) {
					if (isWhite && ((newSquareMask & BitBoards.blackBB) != 0)) {
						//when white, black can be captured
						possibleMoves |= newSquareMask;

					} else if (!isWhite && (newSquareMask & BitBoards.whiteBB) != 0){
						//when black, white can be captured
						possibleMoves |= newSquareMask;
					}
				}
				//same condition check but as one line
				//if (((newSquareMask & (allPieces | attackMask)) == 0) || ((newSquareMask & attackMask) == 0 && ((isWhite && (newSquareMask & allBlack) != 0) || (!isWhite && (newSquareMask & allWhite) != 0)))) {
				//    possibleMoves |= newSquareMask;
				//}
			}
		}
		
		//printMask(possibleMoves);
		
		//Remove moving into check on other side.
		if ((position & attackMask) != 0) {
			possibleMoves &= BitBoards.checkKingInCheckMove(from, isWhite);
		}
		
		//# Castling
		long kingPiece = (isWhite ? BitBoards.whiteKingBB : BitBoards.blackKingBB);
		long castleBoard = previousGameState.getCastleBoard(isWhite);
		
		//check for Castle privilege, have the pieces moved?
		if(castleBoard != 0) {
			long freeSquare = 0L;
			long moveSquare = 0L;

			//King Side Castle
			//add restrictions if in between squares are occupied or attacked
			moveSquare = kingPiece | (kingPiece << 1) | (kingPiece << 2);
			freeSquare = moveSquare &~ kingPiece;
			

			if((freeSquare & BitBoards.allBB) != 0 || ((moveSquare & attackMask) != 0)) {
				castleBoard &= ~((1L << 6) | (1L <<  62));
			}

			//Queen Side Castle
			moveSquare = kingPiece | (kingPiece >> 1) | (kingPiece >> 2);
			freeSquare = (moveSquare | (kingPiece >> 3)) &~ kingPiece;
			
			
			if((freeSquare & BitBoards.allBB) != 0 || ((moveSquare & attackMask) != 0)) {
				castleBoard &= ~((1L << 2) | (1L <<  58));
			}
			possibleMoves |= castleBoard;
		}
		return possibleMoves;
	}


	/**
     * Generates attacks for the king of the same color on the board.
     * Uses BitBoards to retrieve additional Piece Information and as such is static.
     * 
     * @param isWhite Whether the king is white.
     * @return Bitboard representing possible attacks.
     */
	public static long generateSamePieceAttacks(boolean isWhite) {
		long possibleMoves = 0L;
		long newSquareMask = 0L;
		long from = (isWhite ? BitBoards.whiteKingBB : BitBoards.blackKingBB);

		for (int move : kingMoves) {
			// Calculate the new position
			long newPosition = (from << move) | (from >>> -move);

			int oldRank = Long.numberOfTrailingZeros(from) / 8;
			int oldFile = Long.numberOfTrailingZeros(from) % 8;
			int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
			int newFile = Long.numberOfTrailingZeros(newPosition) % 8;  

			//restrict capturing at edge of board
			if (Math.abs(newRank - oldRank) <= 1 &  Math.abs(newFile - oldFile) <= 1) {
				// Create a bit mask for the new square
				newSquareMask = 1L << (Long.numberOfTrailingZeros(newPosition));
			}
			possibleMoves |= newSquareMask;
		}

		return possibleMoves;
	}


	@Override
	public void toggleBB(int square, boolean isWhite){
		BitBoards.kingsBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}

}


