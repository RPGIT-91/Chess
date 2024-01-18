package game.board;

import java.util.Arrays;

import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;

/**
 * The FEN class represents operations related to the Forsyth-Edwards Notation (FEN) in a chess game.
 * It provides methods to generate and parse FEN strings, representing the state of a chess board.
 * 
 * @see game.board.Board
 * @see game.board.GameState
 * 
 * @author Ryu
 * @version 1.0
 */
public class FEN {
	 /**
     * The starting position FEN string for a standard chess game.
     */
	protected static final String START_POSITION_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	protected static final String fileNames = "abcdefgh";

	/**
     * Generates the current FEN string based on the given chess board and game state.
     *
     * @param square    The array representing the chess board squares.
     * @param gameState The current game state.
     * @return The FEN string representing the current state of the chess board.
     */
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
			fen.append(squareNameFromCoordinate(epFileIndex, epRankIndex));
		} else {
			fen.append('-');
		}

		// 50 move counter
		fen.append(' ').append(gameState.getFiftyMoveCounter());

		// Full-move count (should be one at start, and increase after each move by black)
		fen.append(' ').append((gameState.getMoveCounter()));

		return fen.toString();
	}

	
	/**
	 * Loads a chess board position from a Forsyth-Edwards Notation (FEN) string.
	 *
	 * @param fen         The FEN string representing the board position.
	 * @param newGame     Flag indicating whether to clear the game state stack and FEN stack for a new game.
	 * @param pushToStack Flag indicating whether to push the FEN string onto the FEN stack.
	 */
	public static void loadPositionFromFEN(Board board,String fen, boolean newGame, boolean pushToStack) {

		//clear BitBoards.
		Arrays.fill(board.square, null);
		BitBoards.allBB = 0L;
		BitBoards.clear(1);

		if (newGame) {
			board.gameStateStack.clear();
			board.fenStack.clear();
			GameState initialGameState = new GameState(0, 0, 0, true, true, true, true);
			board.saveGameState(initialGameState);
		} 


		String[] sections = fen.split(" ");

		int file = 0;
		int rank = 7;

		for (char symbol : sections[0].toCharArray()) {
			if (symbol == '/') {
				file = 0;
				rank--;
			} else {
				if (Character.isDigit(symbol)) {
					file += Character.getNumericValue(symbol);
				} else {
					int pieceColour = (Character.isUpperCase(symbol)) ? 0 : 1;
					int pieceType;

					switch (Character.toLowerCase(symbol)) {
					case 'p':
						pieceType = 1;
						break;
					case 'n':
						pieceType = 2;
						break;
					case 'b':
						pieceType = 3;
						break;
					case 'r':
						pieceType = 4;
						break;
					case 'q':
						pieceType = 5;				
						break;
					case 'k':
						pieceType = 6;
						break;		
					default:
						pieceType = 0;
						break;
					}

					board.addPiece(rank * 8 + file, pieceType, pieceColour);
					file++;
				}
			}
		}
		try {
			board.gameStateStack.peek().setWhiteToMove(sections[1].equals("w"));

			String castlingRights = sections[2];
			board.gameStateStack.peek().setwKingSideCastle(castlingRights.contains("K"));
			board.gameStateStack.peek().setwQueenSideCastle(castlingRights.contains("Q"));
			board.gameStateStack.peek().setbKingSideCastle(castlingRights.contains("k"));
			board.gameStateStack.peek().setbQueenSideCastle(castlingRights.contains("q"));

			// Default values
			int epFile = -1;
			int fiftyMoveCounter = 0;
			int moveCounter = 0;

			if (sections.length > 3) {
				String enPassantFileName = String.valueOf(sections[3].charAt(0));
				if (FEN.fileNames.contains(enPassantFileName)) {
					epFile = FEN.fileNames.indexOf(enPassantFileName);			
				}
			}

			// Half-move clock
			if (sections.length > 4) {
				fiftyMoveCounter = Integer.parseInt(sections[4]);
			}
			// Full move number
			if (sections.length > 5) {
				moveCounter = Integer.parseInt(sections[5]);
			}

			// plyCounter if black to move +1
			int inc = 0;
			if (!board.gameStateStack.peek().getIsWhiteToMove()) {
				inc = 1;
			}
			board.gameStateStack.peek().setPlyCounter((moveCounter * 2) - 2 + inc);

			board.gameStateStack.peek().setEnPassantFile(epFile);
			board.gameStateStack.peek().setFiftyMoveCounter(fiftyMoveCounter);
			board.gameStateStack.peek().setMoveCounter(moveCounter);

			if (pushToStack) {
				board.pushToFENStack(fen);
			}

		} catch (ArrayIndexOutOfBoundsException e){
			System.err.println("Error: Array index out of bounds. " + e.getMessage());
		}



	}
	/**
     * Converts the given file index and rank index to a square name in FEN format.
     *
     * @param fileIndex The file index (0-7).
     * @param rankIndex The rank index (0-7).
     * @return The FEN square name (e.g., "a1").
     */
	private static String squareNameFromCoordinate(int fileIndex, int rankIndex) {
		return fileNames.charAt(fileIndex) + "" + (rankIndex + 1);
	}

}

