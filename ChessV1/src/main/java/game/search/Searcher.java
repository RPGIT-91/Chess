/**
 * Class responsible for searching the game tree and finding the best move.
 * 
 * Implementation of a negamax search with alpha-beta pruning and quiescence search.
 * 
 * 
 */

package game.search;

import java.util.ArrayList;
import java.util.List;

import game.board.Board;
import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;

public class Searcher {
	// 
	private static final boolean turnOnMoveOrdering = true;
	private static final boolean turnOnAlphaBeta = true;

	//limit quiescence search depth
	private int quiescenceDepth = 10; //ply
	
	private static final boolean showDebugInfo = true;

	public int movesCalculated;
	public Move bestMoveSoFar;
	public int bestEvalSoFar;
	public int bestEvalDepth2;
	public int startingDepth;

	/**
     * Calculates the best move for the given board position and search depth.
     *
     * @param board The current game board.
     * @param depth The search depth.
     */
	public void calcBestMove(Board board, int depth) {
		startingDepth = depth;

		movesCalculated = 0;
		bestMoveSoFar = null;
		int alpha =  -1000001;
		int beta =   1000000;
		//	boolean maximizingPlayer = board.gameStateStack.peek().getIsWhiteToMove();

		bestEvalSoFar = startSearch(board, depth, alpha, beta, true);

		//System.out.println("Best Eval: " + bestEvalSoFar + " Best Move: " + Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo()));
	}


	/**
     * Starts the search algorithm recursively to find the best move.
     *
     * @param board            The current game board.
     * @param depth            The search depth.
     * @param alpha            The alpha value for alpha-beta pruning.
     * @param beta             The beta value for alpha-beta pruning.
     * @param maximizingPlayer Indicates whether the player is maximizing or minimizing, relevant for pruning in quiescence Search
     * @return The evaluation score of the position.
     */
	private int startSearch(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == 0) {
			int counter = 0;
			return quiescenceSearch(board, alpha, beta, counter, maximizingPlayer);
		}

		List<Move> possibleMoves = generateMoves(board);

		// if no moves available return depending on stalemate or checkmate
		if (possibleMoves.isEmpty()) {
			if (BitBoards.isInCheck(false)){
				return -999999;
			}
			return 0;
		}

		if (bestMoveSoFar == null) {
			bestMoveSoFar = possibleMoves.get(0);
		}

		for (Move move : possibleMoves) {
			board.movePiece(move.getFrom(), move.getTo());
			int evaluation = -startSearch(board, depth - 1, -beta, -alpha, !maximizingPlayer);
			board.loadPreviousBoard();

			if (evaluation > alpha) {
				alpha = evaluation;
				if (depth == startingDepth) {
					bestMoveSoFar = move; // Update best move at the starting depth
				}
			}
			if (showDebugInfo) {
				debugInfo(depth, move, alpha, beta, evaluation);	
			}
			
			if (turnOnAlphaBeta) {
				if (evaluation >= beta) {
					break; // Beta cutoff
				}
			}
		}
		return alpha;

	}
	
	/**
     * Performs the quiescence search to handle captures and dynamic evaluation.
     *
     * @param board            The current game board.
     * @param alpha            The alpha value for alpha-beta pruning.
     * @param beta             The beta value for alpha-beta pruning.
     * @param counter          The counter to limit the quiescence search depth.
     * @param maximizingPlayer Indicates whether the player is maximizing or minimizing.
     * @return The evaluation score of the position.
     */
	private int quiescenceSearch(Board board, int alpha, int beta, int counter, boolean maximizingPlayer) {

		// captures aren't forced, so see what the evaluation is without captures
		// Otherwise the position may be evaluated as bad even if good non-capturing moves are available
		movesCalculated++;

		Evaluation eval = new Evaluation(board);
		int evaluation = eval.Evaluate(board.gameStateStack.peek().getIsWhiteToMove());


		//not working as intended
		if (turnOnAlphaBeta) {
			if (evaluation >= beta) {	
				return beta; // Beta cutoff, position is already good enough
				//can run into the issue of not searching further moves when the first capture in a capture chain is not good enough
			}
		}

		alpha = Math.max(evaluation, alpha); // Update alpha if score is higher

		//Limit quiescence search depth artificially
		counter = counter + 1;
		if (counter == quiescenceDepth + 1) {
			System.out.println("        quiescence depth limiter");
			return beta;
		}



		// Generate capturing moves
		List<Move> capturingMoves = generateCaptureMoves(board);

		if (capturingMoves.isEmpty()) {
			return evaluation;
		}

		for (Move capture : capturingMoves) {
			//evaluates the last position to the beta of 


			board.movePiece(capture.getFrom(), capture.getTo());

			//Search recursively for further captures
			evaluation = -quiescenceSearch(board, -beta, -alpha, counter, !maximizingPlayer);

//			Evaluation eval2 = new Evaluation(board);
//			int evaluation2 = -eval2.Evaluate(board.gameStateStack.peek().getIsWhiteToMove());

			board.loadPreviousBoard();



			if (turnOnAlphaBeta) {

				if (!maximizingPlayer) {
					if (evaluation >= beta) {
						if (showDebugInfo) {
							System.out.println("        XXX W   Beta cutoff  " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo())+ ",     alpha: " + alpha + ",     beta: " + beta + ",    current eval: " + evaluation);							
						}
						break; // Beta cutoff
					}
				} else {
					if (evaluation <= beta) {
						if (showDebugInfo) {
							System.out.println("        XXX B   Beta cutoff  " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo())+ ",     alpha: " + alpha + ",     beta: " + beta + ",    current eval: " + evaluation);
						}
						break; // Beta cutoff
					}								
				}

			}

			if (maximizingPlayer) {
				if (evaluation > alpha) {
					alpha = evaluation;
				}
			} else {
				if (evaluation < alpha) {
					alpha = evaluation;
				}
			}



			System.out.println("            capture Move: " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo()) + "                alpha: " + alpha + ",    beta: " + beta + ",      eval: " + evaluation);


		}
		System.out.println("");

		return alpha;
	}

	/**
     * Generates all possible moves for the current side to move.
     *
     * @param board The current game board.
     * @return A list of all possible moves.
     */
	private static List<Move> generateMoves(Board board) {
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
		if (turnOnMoveOrdering) {
			MoveOrdering order = new MoveOrdering(moves);
			order.orderMoves(board, moves);
		}
		return moves;
	}
	
	/**
     * Provides debug information for better understanding of the search process.
     *
     * @param depth      The current search depth.
     * @param move       The move being considered.
     * @param alpha      The alpha value.
     * @param beta       The beta value.
     * @param evaluation The evaluation score.
     */
	private void debugInfo(int depth, Move move, int alpha, int beta, int evaluation) {
		if (depth == 3) {
			System.out.println("Depth 3: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha  + ",     beta: " + beta + ",    current eval: " + evaluation);
			System.out.println("--------------------------------------");
			System.out.println();
			System.out.println();
			System.out.println();
		}

		if (depth == 2) {
			System.out.println("Depth 2: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha  + ",     beta: " + beta + ",    current eval: " + evaluation);
			System.out.println();
			System.out.println();
		}

		if (depth == 1) {
			System.out.println("     Depth 1: " + Board.translateBBToSquare(move.getFrom()) + "-" + Board.translateBBToSquare(move.getTo()) + ",  bestMove: " +  Board.translateBBToSquare(bestMoveSoFar.getFrom()) + "-" + Board.translateBBToSquare(bestMoveSoFar.getTo())+",     alpha: " + alpha + ",     beta: " + beta +",    current eval: " + evaluation);
		}
	}
	
	/**
     * Generates all possible capturing moves for the current side to move.
     *
     * @param board The current game board.
     * @return A list of all capturing moves.
     */
	private static List<Move> generateCaptureMoves(Board board){
		List<Move> captureMoves = generateMoves(board);

		//remove all moves that are not capturing
		boolean colourToMove = board.gameStateStack.peek().getIsWhiteToMove(); 
		for (int i = captureMoves.size() - 1; i >= 0; i--) {
			long moveBoard = 1L << captureMoves.get(i).getTo();
			//			Board.printBitBoard(moveBoard, false);
			//			Board.printBitBoard(moveBoard & BitBoards.blackBB, false);
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

		if (turnOnMoveOrdering) {
			MoveOrdering order = new MoveOrdering(captureMoves);
			order.orderMoves(board, captureMoves);
		}		
		return captureMoves;
	}
}
