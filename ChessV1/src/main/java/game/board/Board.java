package game.board;

import java.util.Stack;

import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;

/**
 * The `Board` class represents a chess board and provides methods for making moves,
 * managing game state, and interacting with bitboards.
 * In a sense it functions similiar to the classical controller.
 * 
 * <p>
 * The board is represented as an 8x8 array of squares, and each square may contain a chess piece.
 * Various methods are provided for adding, moving, and removing pieces, as well as for generating
 * valid moves and managing game state.
 * </p>
 * 
 * <p>
 * Whenever a change is made on the board this class further ensures that the BitBoards inside the BitBoard class are also updated.
 * </p>
 * Position paramaters like to and from are always given in BitBoard annotation.
 * 
 * 
 * @see game.movegeneration.BitBoards
 * @see game.movegeneration.pieces.PieceI
 * @see game.board.GameState
 * @see game.board.FEN
 * 
 * @author Ryu
 * @version 1.0
 */

public class Board {
	public static final int whiteIndex = 0;
	public static final int blackIndex = 1;

	// Stores piece code for each square on the board
	public final PieceI[] square;

	// # Side to move info
	public Stack<GameState> gameStateStack;
	public Stack<String> fenStack;

	//constructor
	public Board() {
		//Game State related
		gameStateStack = new Stack<>();
		fenStack = new Stack<>();

		//Board load up
		square = new PieceI[64];
		loadStartPosition();


		System.out.println("######## Game Start ###########");

	}

	/**
	 * Adds a piece to the chess board at the specified position.
	 * Also syncs the piece to the bitboards.
	 * 
	 * @param pos          The position on the board where the piece will be added (0-63).
	 * @param pieceType    The type of the piece to be added.
	 * @param pieceColour  The color of the piece to be added (0 for white, 1 for black).
	 * 
	 * Add to Square centric Board and to bitboards.
	 */
	public void addPiece(int pos, int pieceType, int pieceColour) {
		int posBB = toBBSquare(pos);
		PieceI newPiece = PieceI.addPiece(pos, pieceType, pieceColour);
		square[posBB] = newPiece;
	}

	/**
	 * Moves a piece from one position to another on the chess board.
	 * Coordinates manipulation of gameState, BitBoards, and Board.
	 * 
	 * @param from  The current position of the piece (0-63).
	 * @param to    The target position for the piece (0-63).
	 */
	public void movePiece(int from, int to) {
		int fromBB = toBBSquare(from);
		int toBB = toBBSquare(to);

		PieceI pieceToMove = square[fromBB];
		PieceI pieceToRemove = square[toBB];

		GameState previousGameState = gameStateStack.peek();
		boolean isWhiteToMove = previousGameState.getIsWhiteToMove(); //Swtich move order.
		int plyCounter = previousGameState.getPlyCounter();

		if (pieceToMove != null) {
			if (pieceToMove.isWhite() == isWhiteToMove) {
				if (pieceToMove.isValidMove(from, to, previousGameState)) {
					// Perform the move if it's valid
					// load relevant information on CurrentGameState
					boolean wKingSide = previousGameState.getwKingSideCastle();
					boolean wQueenSide = previousGameState.getwQueenSideCastle();
					boolean bKingSide = previousGameState.getbKingSideCastle();
					boolean bQueenSide = previousGameState.getbQueenSideCastle();
					int newEnPassantFile = -1;

					square[fromBB] = null; // Remove the piece from the original square
					square[toBB] = pieceToMove; // Place the piece in the new square

					//update BB of Piece.
					pieceToMove.toggleBB(from, pieceToMove.isWhite());				
					pieceToMove.toggleBB(to, pieceToMove.isWhite());


					if (pieceToRemove != null) {
						//when piece is of same type need to turn piece board on again
						if (pieceToMove.getPieceType() == pieceToRemove.getPieceType()) {
							//toggle only piece value.
							pieceToRemove.toggleBB(to, pieceToRemove.isWhite());

							//else toggle target square piece as well as colour board
						} else {
							pieceToRemove.toggleBB(to, pieceToRemove.isWhite());
						}
					}	
					// Special treatment of Pawn double moves and en Passant.
					// if Pawn double move was made --> en Passant may be possible
					if (pieceToMove.getPieceType() == 1) {				
						// logic to include possible EnPassants for next pawn
						if (Math.abs((from / 8) - (to / 8)) == 2) {
							//save 
							newEnPassantFile = (fromBB % 8);
						}				
						// if indeed an en Passant took place remove target piece from square as well as bitboards.
						if (pieceToRemove == null && (fromBB % 8 != toBB % 8)) {
							int shift = (pieceToMove.isWhite() ? 8 : -8);
							pieceToRemove = square[toBB + shift];

							//System.out.println(pieceToRemove.getPieceType());
							pieceToRemove.toggleBB(to - shift, pieceToRemove.isWhite());
							square[toBB + shift] = null;
						}
						//Queening
						if (to / 8 == (pieceToMove.isWhite() ? 7 : 0)) {
							// Check if the pawn reached the 8th (white) or 1st (black) rank
							// Promote the pawn to a queen
							//System.out.println("queening");

							square[toBB] = null;				

							// Update the bitboard for the queen
							pieceToMove.toggleBB(to, pieceToMove.isWhite());

							//BitBoard interferes with adding --> set temporarily to 0;
							BitBoards.allBB = ~1L << to;
							addPiece(to, 5, pieceToMove.getPieceColour()); // Queen's piece type is 5				           			          				          
						}
					} else {
						newEnPassantFile = -1;
					}

					//Also handle castle Moves on the board.
					if ((pieceToMove.getPieceType() == 6)  && (Math.abs(from % 8 - to % 8) == 2)) {
						//hard code the different possibilites
						if (to == 6) {
							//King Side White
							PieceI rookPiece = square[toBBSquare(7)];

							rookPiece.toggleBB(7, rookPiece.isWhite());
							rookPiece.toggleBB(5, rookPiece.isWhite());

							square[toBBSquare(7)] = null;
							addPiece(5, 4, 0);
						} else if (to == 2){
							//Queen Side White
							PieceI rookPiece = square[toBBSquare(0)];

							rookPiece.toggleBB(0, rookPiece.isWhite());
							rookPiece.toggleBB(3, rookPiece.isWhite());

							square[toBBSquare(0)] = null;
							addPiece(3, 4, 0);
						} else if (to == 62) {
							//King Side Black							
							PieceI rookPiece = square[toBBSquare(63)];

							rookPiece.toggleBB(63, rookPiece.isWhite());
							rookPiece.toggleBB(61, rookPiece.isWhite());

							square[toBBSquare(63)] = null;
							addPiece(61, 4, 1);
						} else if (to == 58) {
							//Queen Side Black
							PieceI rookPiece = square[toBBSquare(56)];

							rookPiece.toggleBB(56, rookPiece.isWhite());
							rookPiece.toggleBB(59, rookPiece.isWhite());

							square[toBBSquare(56)] = null;
							addPiece(59, 4, 1);
						}
					}

					// GameState Related Updates and push to stack
					int removedPiece = 0;
					if (pieceToRemove != null) {
						removedPiece = pieceToRemove.getPieceType();

						//remove castling if rook captured on starting position
						if (pieceToRemove.getPieceType() == 4) {
							if (to == 0) {
								wKingSide = false;
							} else if (to == 7) {
								wQueenSide = false;
							} else if (to == 56) {
								bKingSide = false;
							} else if (to == 63) {
								bQueenSide = false;							
							}
						}
					}

					// remove castle right
					if (pieceToMove.getPieceType() == 6) { //King moves
						if (pieceToMove.getPieceColour() == 0) {
							wKingSide = false;
							wQueenSide = false;
						} else {
							bKingSide = false;
							bQueenSide = false;
						}
					}
					if (pieceToMove.getPieceType() == 4) { //Rook moves
						if (from == 0) {
							wKingSide = false;
						} else if (from == 7) {
							wQueenSide = false;
						} else if (from == 56) {
							bKingSide = false;
						} else if (from == 63) {
							bQueenSide = false;							
						}
					}



					plyCounter = plyCounter + 1;

					GameState currentGameState = new GameState(removedPiece, newEnPassantFile, plyCounter, wKingSide, wQueenSide, bKingSide, bQueenSide);								
					currentGameState.setOppToMove(isWhiteToMove);				
					saveGameState(currentGameState);

					BitBoards.updateAll();
					//System.out.println("Move " + moveCounter + " successful   " + from + " - " + to);

					pushToFENStack(FEN.currentFen(square, currentGameState));

				} else {
					// Handle invalid move
					System.out.println("Invalid move:  " + from + " - " + to);
				}
			} else{
				if (pieceToMove.getPieceColour() == 0) {
					System.out.println("Black's turn");
				} else {
					System.out.println("White's turn");
				}
			}
		} else {
			System.out.println("No piece on square:" + from);
		}
		//printBoard(square);

		//target	PtM (empty)		different piece		same piece		
		//piece		0 -> 1			0 -> 1 -> 1			1 -> 0 -> 1
		//							1 -> 1 -> 0		
		//white		0 -> 1			0 -> 1 -> 1			0 -> 1 -> 1
		//black		0 -> 0			1 -> 1 -> 0			1 -> 1 -> 0
	}

	/**
	 * Displays the valid moves for a piece at the specified position.
	 * 
	 * @param from  The position of the piece for which valid moves are to be displayed.
	 * @return      A long value representing the valid moves as a bitboard.
	 */
	public long showValidMoves(int from) {
		int fromBB = toBBSquare(from);
		long validMoves = 0L;
		if (square[fromBB] != null && square[fromBB].isWhite() == gameStateStack.peek().getIsWhiteToMove()) {
			validMoves = square[fromBB].generateMove(from, square[fromBB].isWhite(), gameStateStack.peek());
		}
		return validMoves;
	}


	/**
	 * Loads a chess board position from a Forsyth-Edwards Notation (FEN) string.
	 * 
	 * @param fen The FEN string representing the board position.
	 */
	public void loadFENBoard(String fen) {
		//No check for if valid fen.
		FEN.loadPositionFromFEN(this, fen, false, true);
	}

	/**
	 * Loads the previous chess board position from the game state stack.
	 */
	public void loadPreviousBoard(){
		//pop GameState from stack
		restorePreviousState();

		//pop fen from stack and load 
		fenStack.pop();
		FEN.loadPositionFromFEN(this, fenStack.peek(), false, false);

	}

	// Load the starting position
	/**
	 * Loads the standard starting position of a chess game on the chess board.
	 * The method internally calls {@code LoadPositionFromFEN} with the FEN string
	 * representing the standard starting position.
	 */
	public void loadStartPosition() {
		FEN.loadPositionFromFEN(this, FEN.START_POSITION_FEN, true, true);		
	}

	
	
	
	
	
	// # Helper
	/**
	 * Saves the current game state by pushing it onto the game state stack.
	 *
	 * @param currentGameState The current game state to be saved.
	 */
	protected void saveGameState(GameState currentGameState) {
		// Save the current game state by pushing it onto the stack
		gameStateStack.push(currentGameState);
	}

	//for eval.
	/**
	 * Restores the previous game state by popping from the game state stack.
	 *
	 * @return The restored previous game state, or {@code null} if the stack is empty.
	 */
	private GameState restorePreviousState() {
		// Restore the previous game state by popping from the stack
		if (!gameStateStack.isEmpty()) {
			return gameStateStack.pop();
		} else {
			// Stack is empty, no previous state to restore
			return null;
		}
	}

	/**
	 * Pushes the given FEN string onto the FEN stack.
	 *
	 * @param fen The FEN string to be pushed onto the stack.
	 */
	protected void pushToFENStack(String fen) {
		fenStack.push(fen);
		//System.out.println(fen);
	}	
	
	/**
	 * Inverts the position info. A standard 2D Array is nubered 0-63 but from top to botom unlike the BitBoard which is numbered bottom to top.
	 *
	 * @param pos The position on the chess board (0 to 63).
	 * @return The corresponding index in the bitboard.
	 */
	public static int toBBSquare(int pos) {
		int bbPos = 0;
		int squareIndex = pos / 8;

		switch (squareIndex) {
		case 0:
			bbPos = 7 * 8;
			break;
		case 1:
			bbPos = 6 * 8;
			break;
		case 2: 
			bbPos = 5 * 8;
			break;
		case 3:
			bbPos = 4 * 8;
			break;
		case 4: 
			bbPos = 3 * 8;
			break;
		case 5:
			bbPos = 2 * 8;
			break;
		case 6:
			bbPos = 1 * 8;
			break;
		case 7:
			bbPos = 0 * 8;
			break;
		}
		bbPos = bbPos + pos % 8;
		return bbPos;
	}

	/**
	 * Translates a bitboard index to a chess board square representation.
	 *
	 * @param bb The bitboard index.
	 * @return A string representation of the corresponding chess board square.
	 */
	public static String translateBBToSquare(int bb) {
		int file = bb % 8;
		int rank = bb / 8;

		return FEN.fileNames.charAt(file) + "" + (rank + 1);
	}












	// ################ debugging helper.
	//method to display Board in Console
	
	/**
	 * Debugging: Prints ChessBoard to Console.
	 *
	 * @param square The chess board to print.
	 */
	public void printBoard(PieceI[] square) {
		int count = 0;

		for (PieceI i : square) {
			if (i != null) {
				switch (i.getPieceType() + i.getPieceColour() * 6) {
				case 1: 
					System.out.print("P ");
					break;
				case 2: 
					System.out.print("N ");
					break;
				case 3: 
					System.out.print("B ");
					break;
				case 4: 
					System.out.print("R ");
					break;
				case 5:
					System.out.print("Q ");
					break;
				case 6: 
					System.out.print("K ");
					break;
				case 7: 
					System.out.print("p ");
					break;
				case 8: 
					System.out.print("n ");
					break;
				case 9: 
					System.out.print("b ");
					break;
				case 10: 
					System.out.print("r ");
					break;
				case 11: 
					System.out.print("q ");
					break;
				case 12: 
					System.out.print("k ");
					break;
				default:
					break;
				}
			} else {
				System.out.print(". ");
			}

			count++;

			if (count % 8 == 0) {
				System.out.println(); // Print newline after every 8th element
			}
		}

		System.out.println();
	}

	/**
	 * Debugging: Prints BitBoard to console.
	 *
	 * @param bits The bitboard mask to print.
	 * @param enableIndex If true, prints the index of each square on the board.
	 */
	public static void printBitBoard(long bits, boolean enableIndex) {
		for (int rank = 7; rank >= 0; rank--) {
			for (int file = 0; file < 8; file++) {
				int square = rank * 8 + file;
				long mask = 1L << square;

				if (enableIndex == false) {
					if ((bits & mask) != 0) {
						System.out.print("M ");
					} else {
						System.out.print(". ");
					}      	
				} else {
					if ((bits & mask) != 0) {
						int value = (file + rank * 8 + 1)-1;  // 1-indexed value
						System.out.printf("%2d ", value);
					} else {
						System.out.print(".  ");
					}
				}

			}
			System.out.println();
		}
		System.out.println();
	}


	/**
	 * Debugging: Prints All BitBoards of Pieces and AttackMasks.
	 *
	 * @param enableIndex If true, prints the index of each square on the board.
	 */
	public static void printAllBB(boolean enableIndex) {
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println();
		System.out.println("ALL");
		printBitBoard(BitBoards.allBB, enableIndex);
		System.out.println("WHITE");
		printBitBoard(BitBoards.whiteBB, enableIndex);
		System.out.println("BLACK");
		printBitBoard(BitBoards.blackBB, enableIndex);

		System.out.println("-------- Attack Masks --------");
		System.out.println();
		System.out.println("W - ATTACK");
		printBitBoard(BitBoards.whiteAM, enableIndex);
		System.out.println("B - ATTACK");
		printBitBoard(BitBoards.blackAM, enableIndex);
		printBitBoard(BitBoards.blackPawnsAM, enableIndex);
		printBitBoard(BitBoards.blackRooksAM, enableIndex);
		printBitBoard(BitBoards.blackKnightsAM, enableIndex);
		printBitBoard(BitBoards.blackBishopsAM, enableIndex);
		printBitBoard(BitBoards.blackQueensAM, enableIndex);
		printBitBoard(BitBoards.blackKingAM, enableIndex);

		System.out.println("-------------------------------");
		System.out.println();

		System.out.println("W - PAWN");
		printBitBoard(BitBoards.whitePawnsBB, enableIndex);
		System.out.println("W - KNIGHT");
		printBitBoard(BitBoards.whiteKnightsBB, enableIndex);
		System.out.println("W - BISHOP");
		printBitBoard(BitBoards.whiteBishopsBB, enableIndex);
		System.out.println("W - ROOK");
		printBitBoard(BitBoards.whiteRooksBB, enableIndex);
		System.out.println("W - QUEEN");
		printBitBoard(BitBoards.whiteQueensBB, enableIndex);
		System.out.println("W - KING");
		printBitBoard(BitBoards.whiteKingBB, enableIndex);

		System.out.println("-------------------------------");

		System.out.println();
		System.out.println("B - PAWN");
		printBitBoard(BitBoards.blackPawnsBB, enableIndex);
		System.out.println("B - KNIGHT");
		printBitBoard(BitBoards.blackKnightsBB, enableIndex);
		System.out.println("B - BISHOP");
		printBitBoard(BitBoards.blackBishopsBB, enableIndex);
		System.out.println("B - ROOK");
		printBitBoard(BitBoards.blackRooksBB, enableIndex);
		System.out.println("B - QUEEN");
		printBitBoard(BitBoards.blackQueensBB, enableIndex);
		System.out.println("B - KING");
		printBitBoard(BitBoards.blackKingBB, enableIndex);

		System.out.println("----------- end ---------------");
		System.out.println();
		System.out.println();
	}

}
