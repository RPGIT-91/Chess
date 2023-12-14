package game.board;

// Class to save the current GameState
// who's turn, en Pssant, Castling, fifty move counter.
// captured piece -> important for unmaking moves

public class GameState {
	//not implemented yet.
//	private int repetitionWhite;
//	private int repetitionBlack;
	private int fiftyMoveCounter;

	private boolean isWhiteToMove;
	private int capturedPieceType;
	private int enPassantFile;
	private int plyCounter = 0;
	private int moveCounter;
	
	private boolean wKingSideCastle;
	private boolean wQueenSideCastle;
	private boolean bKingSideCastle;
	private boolean bQueenSideCastle;

	public GameState(int capturedPiece, int enPassantFile, int plyCounter, boolean wKingSideCastle, boolean wQueenSideCastle, boolean bKingSideCastle, boolean bQueenSideCastle) {
		this.capturedPieceType = capturedPiece;
		this.enPassantFile = enPassantFile;
		this.plyCounter = plyCounter;
		this.moveCounter = plyCounter / 2 + 1;
		this.wKingSideCastle = wKingSideCastle;
		this.wQueenSideCastle = wQueenSideCastle;
		this.bKingSideCastle = bKingSideCastle;
		this.bQueenSideCastle = bQueenSideCastle;
		
//		this.repetitionBlack = 0;
//		this.repetitionWhite = 0;
	}
	
	//set a bitboard of possible Castle Squares based on colour and game state
	public long getCastleBoard(boolean isWhite) {
		long castleBoard = 0L;

		if (isWhite) {
			if (wKingSideCastle) {
				//Position of King after Castle QueenSide
				castleBoard |= 1L << 2;
			}
			if (wQueenSideCastle) {
				castleBoard |= 1L << 6;
			}
		} else {
			if (bKingSideCastle) {
				//Position of King after Castle QueenSide
				castleBoard |= 1L << 58;
			}
			if (bQueenSideCastle) {
				castleBoard |= 1L << 62;
			}
		}
		return castleBoard;
	}
	public void resetGameState() {
		resetCastleRight();
		setWhiteToMove(true);
		this.plyCounter = 0;
	}
	
	public void resetCastleRight() {
		wKingSideCastle = true;
		wQueenSideCastle = true;
		bKingSideCastle = true;
		bQueenSideCastle = true;
	}
	
	
	public int getCapturedPieceType() {
		return capturedPieceType;
	}

	public void setCapturedPieceType(int capturedPieceType) {
		this.capturedPieceType = capturedPieceType;
	}
	
	public boolean getwKingSideCastle() {
		return wKingSideCastle;
	}

	public void setwKingSideCastle(boolean isPoss) {
		this.wKingSideCastle = isPoss;
	}
	
	public boolean getwQueenSideCastle() {
		return wQueenSideCastle;
	}

	public void setwQueenSideCastle(boolean isPoss) {
		this.wQueenSideCastle = isPoss;
	}
	
	public boolean getbKingSideCastle() {
		return bKingSideCastle;
	}

	public void setbKingSideCastle(boolean isPoss) {
		this.bKingSideCastle = isPoss;
	}
	
	public boolean getbQueenSideCastle() {
		return bQueenSideCastle;
	}

	public void setbQueenSideCastle(boolean isPoss) {
		this.bQueenSideCastle = isPoss;
	}
	
	public void setPlyCounter(int plyCounter) {
		this.plyCounter = plyCounter;
	}
	
	public int getPlyCounter() {
		return plyCounter;
	}

	public int getEnPassantFile() {
		return enPassantFile;
	}

	public void setEnPassantFile(int enPassantFile) {
		this.enPassantFile = enPassantFile;
	}
	
	public boolean getIsWhiteToMove() {
		return isWhiteToMove;
	}
	
	public void setOppToMove(boolean opponent) {
		this.isWhiteToMove = !opponent;
	}
	
	public void setWhiteToMove(boolean isWhiteToMove) {
		this.isWhiteToMove = isWhiteToMove;
	}
	
	public int getMoveCounter() {
		return moveCounter;
	}
	
	public void setMoveCounter(int moveCounter) {
		this.moveCounter = moveCounter;
	}
	
	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}
	
	public void setFiftyMoveCounter(int fiftyMoveCounter) {
		this.fiftyMoveCounter = fiftyMoveCounter;
	}
}