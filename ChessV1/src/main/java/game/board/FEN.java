package game.board;

import game.movegeneration.pieces.PieceI;

public class FEN {
	public static final String START_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	public static final String fileNames = "abcdefgh";


	public static String currentFen(PieceI[] square, GameState gameState) {
		StringBuilder fen = new StringBuilder();

		int numEmptyFiles = 0;
		for (int i = 0; i < square.length; i++) {
			int file = i % 8;
			int rank = i/8;
			

			if (file == 0) {
				numEmptyFiles = 0;
			}

			int piece = 0;
			if(square[i] != null) {
				piece = square[i].getPieceType();
			} 
			if (piece != 0) {
				if (numEmptyFiles != 0) {
					fen.append(numEmptyFiles);
					numEmptyFiles = 0;
				}
				boolean isWhite = square[i].isWhite();

				char pieceChar = ' ';
				switch (piece) {
				case 1:
					pieceChar = 'P';
					break;
				case 2:
					pieceChar = 'N';
					break;
				case 3:
					pieceChar = 'B';
					break;
				case 4:
					pieceChar = 'R';
					break;
				case 5:
					pieceChar = 'Q';
					break;
				case 6:
					pieceChar = 'K';
					break;
				}
				fen.append(isWhite ? pieceChar : Character.toLowerCase(pieceChar));
			} else {
				numEmptyFiles++;
			}
			
			if (file == 7) {
				if (numEmptyFiles != 0) {
					fen.append(numEmptyFiles);
				}
				
				if (rank != 7) {
					fen.append('/');
				}
			}
		
		}

	// Side to move
	fen.append(' ').append(gameState.getIsWhiteToMove() ? 'w' : 'b');

	// Castling
	boolean whiteKingside = gameState.getwKingSideCastle();
	boolean whiteQueenside = gameState.getwQueenSideCastle();
	boolean blackKingside = gameState.getbKingSideCastle();
	boolean blackQueenside = gameState.getbQueenSideCastle();
	boolean allFalse = false;

	if (!whiteKingside && !whiteQueenside && !blackKingside && !blackQueenside == false) {
		allFalse = true;
	}
	fen.append(' ').append(whiteKingside ? "K" : "").append(whiteQueenside ? "Q" : "").append(blackKingside ? "k" : "").append(blackQueenside ? "q" : "").append(allFalse ? "-" : "");

	// En-passant
	fen.append(' ');
	int epFileIndex = gameState.getEnPassantFile();
	int epRankIndex = gameState.getIsWhiteToMove() ? 5 : 2;

	boolean isEnPassant = epFileIndex != -1;

	if (isEnPassant) {
		fen.append(SquareNameFromCoordinate(epFileIndex, epRankIndex));
	} else {
		fen.append('-');
	}

	// 50 move counter
	fen.append(' ').append(gameState.getFiftyMoveCounter());

	// Full-move count (should be one at start, and increase after each move by black)
	fen.append(' ').append((gameState.getMoveCounter()));

	return fen.toString();
}

public static String SquareNameFromCoordinate(int fileIndex, int rankIndex) {
	return fileNames.charAt(fileIndex) + "" + (rankIndex + 1);
}

}

