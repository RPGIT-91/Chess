package game.search;

import java.util.List;
import java.util.ArrayList;

import game.board.Board;
import game.movegeneration.pieces.PieceI;

public class Searcher {
	
	private static List<Move> moves = new ArrayList<>();;
	
	
	public int CurrentDepth;
//	public Move BestMoveSoFar = bestMove;
//	public int BestEvalSoFar = bestEval;
	
	public static int makeAllMoves(Board board) {
		int movesMade = 0;
		
		generateMoves(board);
		
		for (Move i : moves) {
			board.movePiece(i.getFrom(), i.getTo());
			
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			board.loadPreviousBoard();
			
			movesMade++;
		}
		return movesMade;
	}

	//only generate for colour to move
	private static void generateMoves(Board board) {
		PieceI[] currentBoard = board.square;
		moves.clear();
		
		for (int from = 0; from < currentBoard.length; from++) {
			int fromBB = Board.toBBSquare(from);
			boolean isWhiteToMove = board.gameStateStack.peek().getIsWhiteToMove();

			if (currentBoard[fromBB] != null && currentBoard[fromBB].isWhite() == isWhiteToMove) {
				long validMoves = currentBoard[fromBB].generateMove(from, currentBoard[fromBB].isWhite(), board.gameStateStack.peek());

				// Process valid moves here, you can add them to a list or perform other actions
				// For example, you can iterate through bits in validMoves and process each move
				
				while (validMoves != 0) {
					long leastSignificantBit = validMoves & -validMoves;
					int to = Long.numberOfTrailingZeros(leastSignificantBit);
					Move move = new Move(from, to);
					moves.add(move);
					validMoves ^= leastSignificantBit;  // Clear the least significant bit
					
					System.out.println(move.getFrom() + " " + move.getTo());
				}
				
//				for (int to = 0; to < 64; to++) {
//					if ((validMoves & (1L << to)) != 0) {
//						// Process the move or add it to a list
//						// For example, you might want to create a Move object and store it
//						Move move = new Move(from, to);
//						// Add the move to a list or perform other actions
//						System.out.println(move.getFrom());
//						moves.add(move);
//					}
//				}
			}
		}
	}
}
