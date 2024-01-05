package game.search;

import java.util.List;
import java.util.ArrayList;
//import java.util.Random;

import game.board.Board;
import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;

public class Searcher {
	public static int movesCalculated;
	public static Move bestMoveSoFar;
	public static int bestEvalSoFar;
	public static int bestEvalDepth2;
	public static int startingDepth;

	public static void calcBestMove(Board board, int depth) {
		startingDepth = depth;

		movesCalculated = 0;
		bestMoveSoFar = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		//	boolean maximizingPlayer = board.gameStateStack.peek().getIsWhiteToMove();

		bestEvalSoFar = startSearch(board, depth, alpha, beta, true);

		System.out.println("Best Eval: " + bestEvalSoFar + " Best Move: " + Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo()));
	}


	//maximizing player starts as true and is switched after every move as to minimize the opponents score
	// i.e. 
	public static int startSearch(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0) {
			movesCalculated++;
			//keep searching until the position is quiet
			int evaluation = quiescenceSearch(board, alpha, beta, maximizingPlayer);

			return evaluation;
		} else {
			List<Move> possibleMoves = generateMoves(board);
			//if no bestmove selected yet, set it to the first move, just in case it breaks.
			if (bestMoveSoFar == null) {
				bestMoveSoFar = possibleMoves.get(0);
			}

			if (maximizingPlayer) {
				int maxEval = Integer.MIN_VALUE;

				for (Move move : possibleMoves) {          
					board.movePiece(move.getFrom(), move.getTo());           
					//recursive search til depth of 0
					int eval = startSearch(board, depth - 1, alpha, beta, false);

					board.loadPreviousBoard();

					//Evaluate final position and save move if new best move is found           
					if (eval > maxEval) {
						maxEval = eval;
						if (depth == startingDepth) {
							bestMoveSoFar = move; // Update best move at the starting depth
						}
					}
					System.out.println("MaxEval: " + maxEval + " eval: " + eval);

					alpha = Math.max(alpha, eval);



					if (depth == 2) {
						System.out.println("Depth 2: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha  + ",     beta: " + beta + ",    current eval: " + eval);
						System.out.println();
						System.out.println();
					}

					if (depth == 1) {
						System.out.println("     Depth 1: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha + ",     beta: " + beta +",    current eval: " + eval);
						System.out.println();
					}


					if (beta <= alpha) {
						break; // Beta cut-off
					}
				}
				return maxEval;

			} else {
				int minEval = Integer.MAX_VALUE;

				for (Move move : possibleMoves) {
					board.movePiece(move.getFrom(), move.getTo());
					int eval = startSearch(board, depth - 1, alpha, beta, true);
					board.loadPreviousBoard();

					minEval = Math.min(minEval, eval);
					beta = Math.min(beta, eval);

					if (depth == 2) {
						System.out.println("Depth 2: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha  + ",     beta: " + beta + ",    current eval: " + eval);
						System.out.println();
						System.out.println();
					}

					if (depth == 1) {
						System.out.println("     Depth 1: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha + ",     beta: " + beta +",    current eval: " + eval);
					}

					if (beta <= alpha) {
						break; // Alpha cut-off
					}
				}
				return minEval;
			}
		}

	}

	public static int quiescenceSearch(Board board, int alpha, int beta, boolean maximizingPlayer) {
		// captures aren't forced, so see what the evaluation is without captures
		// Otherwise the position may be evaluated as bad even if good non-capturing moves are available

		Evaluation eval = new Evaluation(board);
		//System.out.println("Evaluation from white: " + board.gameStateStack.peek().getIsWhiteToMove() + " is maximizer: " + maximizingPlayer);
		int evaluation = eval.Evaluate(board.gameStateStack.peek().getIsWhiteToMove());		//evaluation 
		
		
		List<Move> capturingMoves = generateCaptureMoves(board);
		
		// if no captures available return current evaluation
		if (capturingMoves.isEmpty()) {
			if (maximizingPlayer) {
				return evaluation;
			} else {
				return evaluation;
			}
		}
		
		System.out.println();
		System.out.println("       Commencing with Quiesence Search Results. " +  "white to Move: " + board.gameStateStack.peek().getIsWhiteToMove());

		if (maximizingPlayer) {
			
			//sth here fix
//			if (evaluation >= beta) {
//				return beta; // Beta cutoff, position is already good enough
//			}
			alpha = Math.max(evaluation, alpha);

			int maxEval = Integer.MIN_VALUE;
			
			
			for (Move capture : capturingMoves) {
				board.movePiece(capture.getFrom(), capture.getTo());

				//Search recursively for further captures
				evaluation = quiescenceSearch(board, beta, alpha, false);
				board.loadPreviousBoard();
				
				maxEval = Math.max(maxEval, evaluation);
				alpha = Math.max(alpha, evaluation);

				if (evaluation >= beta) {
					break; // Beta cutoff
				}

				// Update alpha if score is higher
				alpha = Math.max(evaluation, alpha); 
				System.out.println("               capture Move: " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo())+ " evaluation: " + evaluation);
				

			}	     

			return alpha;

		} else {
//			if (evaluation <= alpha) {
//				return alpha; // Alpha cutoff, position is already good enough
//			}
			beta = Math.min(evaluation, beta);


			int minEval = Integer.MAX_VALUE;
			for (Move capture : capturingMoves) {
				board.movePiece(capture.getFrom(), capture.getTo());

				// Search recursively for further captures
				evaluation = quiescenceSearch(board, beta, alpha, true);

				board.loadPreviousBoard();

				minEval = Math.min(minEval, evaluation);
				beta = Math.min(beta, evaluation);                                

				if (evaluation <= alpha) {
					break; // Alpha cut-off
				}
				System.out.println("               capture Move: " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo())+ " evaluation: " + evaluation);
				
			}
			return beta;				
		}

	}

	public static int quiescenceSearchBase(Board board, int alpha, int beta, boolean maximizingPlayer) {
		// captures aren't forced, so see what the evaluation is without captures
		// Otherwise the position may be evaluated as bad even if good non-capturing moves are available

		Evaluation eval = new Evaluation(board);

		int evaluation = eval.Evaluate(true);

		if (evaluation >= beta) {
			return beta; // Beta cutoff, position is already good enough
		}
		alpha = Math.max(evaluation, alpha);


		// Generate capturing moves
		List<Move> capturingMoves = generateCaptureMoves(board);

		for (Move capture : capturingMoves) {
			System.out.println("capture Move: " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo()));
			board.movePiece(capture.getFrom(), capture.getTo());

			//Search recursively for further captures
			evaluation = -quiescenceSearch(board, -beta, -alpha, true);

			board.loadPreviousBoard();

			if (evaluation >= beta) {
				return beta; // Beta cutoff
			}

			// Update alpha if score is higher
			alpha = Math.max(evaluation, alpha); 

		}


		return alpha;
	}

	//only generate for colour to move
	public static List<Move> generateMoves(Board board) {
		PieceI[] currentBoard = board.square;
		List<Move> moves = new ArrayList<>();

		boolean isWhiteToMove = board.gameStateStack.peek().getIsWhiteToMove();

		for (int from = 0; from < currentBoard.length; from++) {
			int fromBB = Board.toBBSquare(from);

			if (currentBoard[fromBB] != null && currentBoard[fromBB].isWhite() == isWhiteToMove) {
				long validMoves = currentBoard[fromBB].generateMove(from, currentBoard[fromBB].isWhite(), board.gameStateStack.peek());

				while (validMoves != 0) {
					long leastSignificantBit = validMoves & -validMoves;
					int to = Long.numberOfTrailingZeros(leastSignificantBit);
					Move move = new Move(from, to);
					moves.add(move);
					validMoves ^= leastSignificantBit;  // Clear the least significant bit

					// System.out.println(move.getFrom() + " " + move.getTo());
				}		
			}
		}
		return moves;
	}

	public static List<Move> generateCaptureMoves(Board board){
		List<Move> captureMoves = generateMoves(board);

		//remove all moves that are not capturing
		boolean colourToMove = board.gameStateStack.peek().getIsWhiteToMove(); 
		for (int i = captureMoves.size() - 1; i >= 0; i--) {
			long moveBoard = 1L << captureMoves.get(i).getTo();
			//			Board.printBitBoard(moveBoard, false);
			//remove all non captures
			if (colourToMove) {
				//check if moveTo is a capture of a black piece, remove if not
				if ((moveBoard & BitBoards.blackBB) == 0) {
					captureMoves.remove(i);
				}
			} else {
				//check if moveTo is a capture of a white piece, remove if not
				if ((moveBoard & BitBoards.whiteBB) == 0) {
					captureMoves.remove(i);
				}
			}
		}
		return captureMoves;
	}


	//belongs into before loadPreviousBoard
	//	if (depth == 5) {
	//				System.out.println();
	//				System.out.println("Depth 5 " + Board.translateBBToSquare(searchMoves.getFrom()) + " " + Board.translateBBToSquare(searchMoves.getTo()));
	//			}
	//			
	//			if (depth == 4) {
	//				System.out.println();
	//				System.out.println("------ Depth 4  " + Board.translateBBToSquare(searchMoves.getFrom()) + " " + Board.translateBBToSquare(searchMoves.getTo()));
	//			}
	//			
	//			
	//			if (depth == 3) {
	//				System.out.println();
	//				System.out.println("Depth 3 " + Board.translateBBToSquare(searchMoves.getFrom()) + " " + Board.translateBBToSquare(searchMoves.getTo()));
	//				
	//			}
	//
	//			if (depth == 2) {
	//				//System.out.println("Response: " + response);
	//				System.out.println(" - "+ board.square[Board.toBBSquare(searchMoves.getFrom())].getPieceType() + " "+ Board.translateBBToSquare(searchMoves.getFrom()) + " " + Board.translateBBToSquare(searchMoves.getTo()));
	//				response = 0;
	//				
	//				
	//				pawnCounter = 0;
	//				knightCounter = 0;
	//				bishopCounter = 0;
	//				rookCounter = 0;
	//				queenCounter = 0;
	//				kingCounter = 0;
	//			}
	//			
	//			if (depth == 1) {				
	//				
	//				int pieceType = board.square[Board.toBBSquare(searchMoves.getFrom())].getPieceType();
	//				if (pieceType == 6) {
	//					kingCounter++;
	//				} else if (pieceType == 5) {
	//					queenCounter++;
	//				} else if (pieceType == 4) {
	//					rookCounter++;
	//				} else if (pieceType == 3) {
	//					bishopCounter++;
	//				} else if (pieceType == 2) {
	//					knightCounter++;
	//				} else if (pieceType == 1) {
	//					pawnCounter++;
	//				} 
	//				
	//				if (pawnCounter + knightCounter + bishopCounter + rookCounter + queenCounter + kingCounter == listMoves.size()) {
	//					 System.out.println("Total moves: " + listMoves.size());
	//					System.out.println("Pawn Moves: " + pawnCounter);
	//					System.out.println("Knight Moves: " + knightCounter);
	//					System.out.println("Bishop Moves: " + bishopCounter);
	//					System.out.println("Rook Moves: " + rookCounter);
	//					System.out.println("Queen Moves: " + queenCounter);
	//					System.out.println("King Moves: " + kingCounter);
	//					System.out.println();
	//				}
	//				//System.out.println( board.square[Board.toBBSquare(searchMoves.getFrom())].getPieceType() + " -- " + Board.translateBBToSquare(searchMoves.getFrom()) + " " + Board.translateBBToSquare(searchMoves.getTo()));
	//				response += listMoves.size();
	//				
	//			}	

	//	
	//	public static void makeRandomMove(Board board) {
	//		generateMoves(board);
	//		System.out.println(moves.size());
	//		int rnd = random.nextInt(moves.size() - 1);
	//		board.movePiece(moves.get(rnd +1).getFrom(), moves.get(rnd + 1).getTo());
	//	}
}
