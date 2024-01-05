package game.search;

import game.board.Board;


//Class to return current evaluation of position
public class Evaluation {
	public MaterialInfo whiteMaterial;
	public MaterialInfo blackMaterial;
	
	
	public boolean isWhite;
	
	public int materialScore;

	public int whiteEval;
	public int blackEval;
	
	
	// initialize Material Info for position
	public Evaluation(Board board) {
		isWhite = board.gameStateStack.peek().getIsWhiteToMove();
		
		whiteMaterial = new MaterialInfo(true);
		blackMaterial = new MaterialInfo(false);
//		System.out.println();
//		System.out.println("White");
//		System.out.println(whiteMaterial.Sum());
//		System.out.println("Black");
//		System.out.println(blackMaterial.Sum());
	}

	// Performs static evaluation of the current position.
	// The position is assumed to be 'quiet', i.e no captures are available that could drastically affect the evaluation.
	// The score that's returned is given from the perspective of whoever's turn it is to move.
	// So a positive score means the player who's turn it is to move has an advantage, while a negative score indicates a disadvantage.
	public int Evaluate(boolean isWhite) {
		int perspective = isWhite ? 1 : -1;
//		System.out.print("White: " + whiteMaterial.Sum() + " Black: " + blackMaterial.Sum() + " ");
		int eval = whiteMaterial.Sum() - blackMaterial.Sum();
		return eval * perspective;
	}

}