package game.movegeneration.pieces;

import game.movegeneration.*;
import game.board.GameState;

public class Pawn implements PieceI {
	public static long pawnAttacks;

	private final int pieceType = 1; // Unique identifier for Pawn
	private final int pieceColour; // 0 for white, 1 for black

	//Constructor
	public Pawn(int pieceColour, int pos) {
		this.pieceColour = pieceColour;

		//construct bitBoard
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
		long possibleMoves = 0L;
		long bitboard = 1L << from;
		long caps = pawnAttacks(bitboard, isWhite);

		if (BitBoards.doubleCheck(isWhite)) {
			// Calculate possible moves for a pawn
			if (isWhite) {
				// White pawn moves	
				long singleMove = bitboard << 8;
				if ((singleMove & BitBoards.allBB) == 0) {
					possibleMoves |= singleMove;

					//double Move if not blocked and rank2
					if ((bitboard & rank2) != 0) {
						long doubleMove = bitboard << 16;
						if ((doubleMove & BitBoards.allBB) == 0) {
							possibleMoves |= doubleMove;
						}
					}
				}
				//diagonal captures
				possibleMoves |= (caps & BitBoards.blackBB);
			} else {
				//Black Pawn moves
				long singleMove = bitboard >> 8;
						if ((singleMove & BitBoards.allBB) == 0) {
							possibleMoves |= singleMove;

							//double Move if not blocked and rank7
							if ((bitboard & rank7) != 0) {
								long doubleMove = bitboard >> 16;

				if ((doubleMove & BitBoards.allBB) == 0) {
					possibleMoves |= doubleMove;
				}
							}
						}
						//diagonal captures
						possibleMoves |= (caps & BitBoards.whiteBB);
			}

			// include enPassant captures,
			long rank3 = (0xFFL << (2 * 8));
			long rank6 = (0xFFL << (5 * 8));
			long vertical = (0x0101010101010101L << (previousGameState.getEnPassantFile() % 8) & ~(1L << previousGameState.getEnPassantFile()));
			//long vertical = (0x0101010101010101L << (-1 % 8) & ~(1L << -1));
			
			
			vertical &= (rank3 | rank6);
			if((vertical & caps) != 0) {
				//Remove en passant capture when that would result in a check
				if (BitBoards.checkEnPassantPin(from, isWhite, previousGameState.getEnPassantFile())) {					
					long pawn = (isWhite ? BitBoards.whitePawnsBB: BitBoards.blackPawnsBB);
					//exclude white caps to rank 3 and black to 6 when EP					
					if ((bitboard & pawn & rank6 >> 8) != 0) {
						possibleMoves |= (vertical & caps & ~rank3);
					} else if ((bitboard & pawn & rank3 << 8) != 0) {
						possibleMoves |= (vertical & caps & ~rank6);
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
	
	


	public static long generateSamePieceAttacks(boolean isWhite) {
		if (isWhite) {
			pawnAttacks = ((BitBoards.whitePawnsBB << 9) & notAFile) | ((BitBoards.whitePawnsBB << 7) & notHFile);
			return pawnAttacks;
		} else {
			pawnAttacks = ((BitBoards.blackPawnsBB >> 7) & notAFile) | ((BitBoards.blackPawnsBB >> 9) & notHFile);
			return pawnAttacks;
		}

	}

	//Helper Methods
	@Override
	public void toggleBB(int square, boolean isWhite){
		BitBoards.pawnsBB ^= 1L << square;

		if (isWhite) {
			BitBoards.whiteBB ^= 1L << square;
		} else {
			BitBoards.blackBB ^= 1L << square;
		}
	}


	private static long pawnAttacks(long pawnBitboard, boolean isWhite) {
		if (isWhite) {
			return ((pawnBitboard << 9) & notAFile) | ((pawnBitboard << 7) & notHFile);
		} else {
			return ((pawnBitboard >> 7) & notAFile) | ((pawnBitboard >> 9) & notHFile);
		}
	}
}
