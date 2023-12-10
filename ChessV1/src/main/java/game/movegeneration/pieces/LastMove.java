//since fields declared in an interface are implicitly public, static or final they cannot be reassigned in the implementing classes.
//Hold the information that is supposed to be shared across different 

package game.movegeneration.pieces;

public class LastMove {
	private static int counter = 1;
	private static String lastMove = null;
	private static boolean enPassant = false;
	private static long enPassantBoard = 0L;

	private static boolean castled = false;
	private static long whiteKing = 1L << 4;
	private static long whiteKingRook = 1L << 7;
	private static long whiteQueenRook = 1L << 0;
	private static long blackKing = 1L << 60;
	private static long blackKingRook = 1L << 56;
	private static long blackQueenRook = 1L << 63;
	private static boolean wKingSide = true;
	private static boolean wQueenSide = true;
	private static boolean bKingSide = true;
	private static boolean bQueenSide = true;

	public static int getCounter() {
		return counter;
	}

	public static void incrementCounter() {
		counter++;
	}

	public static String getLastMove() {
		return lastMove;
	}

	public static void setLastMove(String fromSquare, String toSquare) {
		lastMove = fromSquare + "-" + toSquare;
	}

	public static void setLastMoveNull() {
		lastMove = null;
	}

	public static boolean getEnPassant() {
		return enPassant;
	}
	//Special directions for Pawn moves.
	public static void setEnPassantMade() {
		enPassant = true;
	}

	public static void setEnPassantMadeFalse() {
		enPassant = false;
	}

	public static long getEnPassantBoard() {
		return enPassantBoard;
	}
	public static void setEnPassantBoard(long enPassantPossible) {
		enPassantBoard = enPassantPossible;
	}
	
	
	//Castle saved here, as Castle privileges are affected by both rooks, Kings, as well as pieces in between.
	public static boolean getCastle() {
		return castled;
	}

	public static void setCastlePossible(long pieceBoard, long allWhite, long allBlack) {
		//for white
		if((pieceBoard & allWhite)!= 0) {
			//if rook moved the king or queen side castle rights are no longer true
			if((pieceBoard & whiteQueenRook)!= 0) {
				wKingSide = false;
			} else if((pieceBoard & whiteKingRook)!= 0) {
				wQueenSide = false;	
			}
			//if king moves both side castle rights are no longer true
			else if((pieceBoard & whiteKing)!= 0) {
				wQueenSide = false;
				wKingSide = false;
			} 
		// for Black
		} else {
			//if rook moved the king or queen side castle rights are no longer true
			if((pieceBoard & blackQueenRook)!= 0) {
				bKingSide = false;
			} else if((pieceBoard & blackKingRook)!= 0) {
				bQueenSide = false;
			}
			//if king moves both side castle rights are no longer true
			else if((pieceBoard & blackKing)!= 0) {
				bQueenSide = false;
				bKingSide = false;
			}
		}
	}

	
	public static boolean isCastlePossible(long kingBoard) {
		boolean possible = false;
		if((kingBoard & whiteKing) != 0) {
			if(wKingSide | wQueenSide) {
				possible = true;
			}
		} else if((kingBoard & blackKing) != 0) {
			if(bKingSide | bQueenSide) {
				possible = true;
			}
		}
		return possible;
	}

}
