package game.board;

import java.util.Arrays;
import java.util.Stack;

import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;


public class Board {
	public static final int whiteIndex = 0;
	public static final int blackIndex = 1;

	// Stores piece code for each square on the board
	public final PieceI[] square;

	// # Side to move info
	public Stack<GameState> gameStateStack;
	public boolean isWhiteToMove;
	public int moveCounter;
	public int plyCounter;

	//constructor
	public Board() {
		//Game State related
		gameStateStack = new Stack<>();
		GameState initialGameState = new GameState(0, 0, 0, true, true, true, true);
		saveGameState(initialGameState);

		//Board load up
		square = new PieceI[64];
		loadStartPosition();


		System.out.println("######## Game Start ###########");

	}

	//Add to Square centric Board and to bitboards.
	public void addPiece(int pos, int pieceType, int pieceColour) {
		int posBB = toBBSquare(pos);
		PieceI newPiece = PieceI.addPiece(pos, pieceType, pieceColour);
		square[posBB] = newPiece;
	}


	public void movePiece(int from, int to) {
		int fromBB = toBBSquare(from);
		int toBB = toBBSquare(to);

		PieceI pieceToMove = square[fromBB];
		PieceI pieceToRemove = square[toBB];

		GameState previousGameState = gameStateStack.peek();
		isWhiteToMove = previousGameState.getIsWhiteToMove(); //Swtich move order.
		plyCounter = previousGameState.getPlyCounter();
		moveCounter = previousGameState.getMoveCounter();

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
							newEnPassantFile = (fromBB % 8); //correct for -1.
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
							System.out.println("queening");

							square[toBB] = null;				

							// Update the bitboard for the queen
							pieceToMove.toggleBB(to, pieceToMove.isWhite());

							//BitBoard interferes with adding --> set temporarily to 0;
							BitBoards.allBB = ~1L << to;
							addPiece(to, 5, pieceToMove.getPieceColour()); // Queen's piece type is 5				           			          				          
						}
					} else {
						newEnPassantFile = 0;
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
					System.out.println("Move " + moveCounter + " successful   " + from + " - " + to);
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
		printBoard(square);

		//target	PtM (empty)		different piece		same piece		
		//piece		0 -> 1			0 -> 1 -> 1			1 -> 0 -> 1
		//							1 -> 1 -> 0		
		//white		0 -> 1			0 -> 1 -> 1			0 -> 1 -> 1
		//black		0 -> 0			1 -> 1 -> 0			1 -> 1 -> 0
	}

	public long showValidMoves(int from) {
		int fromBB = toBBSquare(from);
		long validMoves = 0L;
		if (square[fromBB] != null && square[fromBB].isWhite() != isWhiteToMove) {
			validMoves = square[fromBB].generateMove(from, square[fromBB].isWhite(), gameStateStack.peek());
		}
		return validMoves;
	}

	// Load the starting position
	public void loadStartPosition() {
		//      loadPosition(FenUtility.START_POSITION_FEN);
		gameStateStack.peek().resetGameState();
		gameStateStack.peek().setEnPassantFile(-1);

		//clear Board for new game
		Arrays.fill(square, null);
		BitBoards.allBB = 0L;
		BitBoards.clear(1); //basically updates all BitBoards. Slightly different to the usual updateall method.

		addPiece(0, 4, 0);
		addPiece(1, 2, 0);
		addPiece(2, 3, 0);
		addPiece(3, 5, 0);
		addPiece(4, 6, 0);
		addPiece(5, 3, 0);
		addPiece(6, 2, 0);
		addPiece(7, 4, 0);
		addPiece(8, 1, 0);
		addPiece(9, 1, 0);
		addPiece(10, 1, 0);
		addPiece(11, 1, 0);
		addPiece(12, 1, 0);
		addPiece(13, 1, 0);
		addPiece(14, 1, 0);
		addPiece(15, 1, 0);

		addPiece(48, 1, 1);
		addPiece(49, 1, 1);
		addPiece(50, 1, 1);
		addPiece(51, 1, 1);
		addPiece(52, 1, 1);
		addPiece(53, 1, 1);
		addPiece(54, 1, 1);
		addPiece(55, 1, 1);
		addPiece(56, 4, 1);
		addPiece(57, 2, 1);
		addPiece(58, 3, 1);
		addPiece(59, 5, 1);
		addPiece(60, 6, 1);
		addPiece(61, 3, 1);
		addPiece(62, 2, 1);
		addPiece(63, 4, 1);
	}

	// # Helper
	private static int toBBSquare(int pos) {
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

	private void saveGameState(GameState currentGameState) {
		// Save the current game state by pushing it onto the stack
		gameStateStack.push(currentGameState);
	}

	//for eval.
	private GameState restorePreviousState() {
		// Restore the previous game state by popping from the stack
		if (!gameStateStack.isEmpty()) {
			return gameStateStack.pop();
		} else {
			// Stack is empty, no previous state to restore
			return null;
		}
	}


	// # debugging
	//method to display Board in Console
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


	//For debugging bitBoard Synchro
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

		//		System.out.println("-------- Attack Masks --------");
		//		System.out.println();
		//		System.out.println("W - ATTACK");
		//		printBitBoard(BitBoards.whiteAM, enableIndex);
		//		System.out.println("B - ATTACK");
		//		printBitBoard(BitBoards.blackAM, enableIndex);
		//		printBitBoard(BitBoards.blackPawnsAM, enableIndex);
		//		printBitBoard(BitBoards.blackRooksAM, enableIndex);
		//		printBitBoard(BitBoards.blackKnightsAM, enableIndex);
		//		printBitBoard(BitBoards.blackBishopsAM, enableIndex);
		//		printBitBoard(BitBoards.blackQueensAM, enableIndex);
		//		printBitBoard(BitBoards.blackKingAM, enableIndex);

		System.out.println("-------------------------------");
		System.out.println();

		System.out.println("W - PAWN");
		printBitBoard(BitBoards.whitePawnsBB, enableIndex);
		//		System.out.println("W - KNIGHT");
		//		printBitBoard(BitBoards.whiteKnightsBB, enableIndex);
		//		System.out.println("W - BISHOP");
		//		printBitBoard(BitBoards.whiteBishopsBB, enableIndex);
		//		System.out.println("W - ROOK");
		//		printBitBoard(BitBoards.whiteRooksBB, enableIndex);
		System.out.println("W - QUEEN");
		printBitBoard(BitBoards.whiteQueensBB, enableIndex);
		//		System.out.println("W - KING");
		//		printBitBoard(BitBoards.whiteKingBB, enableIndex);

		System.out.println("-------------------------------");

		System.out.println();
		//		System.out.println("B - PAWN");
		//		printBitBoard(BitBoards.blackPawnsBB, enableIndex);
		//		System.out.println("B - KNIGHT");
		//		printBitBoard(BitBoards.blackKnightsBB, enableIndex);
		//		System.out.println("B - BISHOP");
		//		printBitBoard(BitBoards.blackBishopsBB, enableIndex);
		//		System.out.println("B - ROOK");
		//		printBitBoard(BitBoards.blackRooksBB, enableIndex);
		//		System.out.println("B - QUEEN");
		//		printBitBoard(BitBoards.blackQueensBB, enableIndex);
		//		System.out.println("B - KING");
		//		printBitBoard(BitBoards.blackKingBB, enableIndex);

		System.out.println("----------- end ---------------");
		System.out.println();
		System.out.println();
	}
	//    public void loadPosition(String fen) {
	//        FenUtility.PositionInfo posInfo = FenUtility.positionFromFen(fen);
	//        loadPosition(posInfo);
	//    }



}
