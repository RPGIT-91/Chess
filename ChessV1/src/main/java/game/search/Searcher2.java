package game.search;

import java.util.ArrayList;
import java.util.List;

import game.board.Board;
import game.movegeneration.BitBoards;
import game.movegeneration.pieces.PieceI;

public class Searcher2 {
	public int movesCalculated;
	public Move bestMoveSoFar;
	public int bestEvalSoFar;
	public int bestEvalDepth2;
	public int startingDepth;
	
	public int quiescenceDepth = 4;

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


	//maximizing player starts as true and is switched after every move as to minimize the opponents score
	public int startSearch(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == 0) {
			int counter = 0;
			return quiescenceSearch(board, alpha, beta, counter);
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

			if (evaluation >= beta) {
				break; // Beta cutoff
			}
		}
		return alpha;

	}
	public int quiescenceSearch(Board board, int alpha, int beta, int counter) {
		
		//limit quiescence search depth
		
		
		// captures aren't forced, so see what the evaluation is without captures
		// Otherwise the position may be evaluated as bad even if good non-capturing moves are available

		movesCalculated++;
		
		
		counter++;

		if (counter == quiescenceDepth) {
			System.out.println("        quiescence depth reached");
			return beta;
		}
		Evaluation eval = new Evaluation(board);

		int evaluation = eval.Evaluate(board.gameStateStack.peek().getIsWhiteToMove());
		//
		
		// issues arise when enemy only has one option to recapture, and it is a bad move
		if (evaluation >= beta) {
			//System.out.println("                  beta cutoff" + ",     alpha: " + alpha + ",     beta: " + beta + ",    current eval: " + evaluation);
			return beta; // Beta cutoff, position is already good enough
		}


		alpha = Math.max(evaluation, alpha); // Update alpha if score is higher




		// Generate capturing moves
		List<Move> capturingMoves = generateCaptureMoves(board);

		if (capturingMoves.isEmpty()) {
			return evaluation;
		}

		for (Move capture : capturingMoves) {
			//evaluates the last position to the beta of 


			board.movePiece(capture.getFrom(), capture.getTo());

			//Search recursively for further captures
			evaluation = -quiescenceSearch(board, -beta, -alpha, counter);

			board.loadPreviousBoard();

			if (evaluation > alpha) {
				alpha = evaluation;
			}

			System.out.println("            capture Move: " + Board.translateBBToSquare(capture.getFrom()) + " - " + Board.translateBBToSquare(capture.getTo()) + "                alpha: " + alpha + ",    beta: " + beta + ",      eval: " + evaluation);
			//			if (evaluation >= beta) {
			//				System.out.println("        XXX    Beta cutoff" + ",     alpha: " + alpha + ",     beta: " + beta + ",    current eval: " + evaluation);
			//				break; // Beta cutoff
			//			}






		}
		System.out.println("");

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
		return captureMoves;
	}
}
