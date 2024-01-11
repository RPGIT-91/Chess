package game.search;

import game.board.Board;
import game.movegeneration.BitBoards;

/**
 * Class to compute the current evaluation of a chess position.
 * 
 * @see game.search.Searcher
 * @see game.search.MaterialInfo
 * 
 * @author Ryu
 * @version 1.0
 */
public class Evaluation {
	/**
     * Information about material for the white side.
     */
	private MaterialInfo whiteMaterial;
	/**
     * Information about material for the black side.
     */
	private MaterialInfo blackMaterial;
	
	/**
     * Mop-up score for the white side.
     */
	private int mopUpWhite;
	/**
     * Mop-up score for the black side.
     */
	private int mopUpBlack;
	
	/**
     * Flag indicating whether the side being evaluated is white.
     */
	public boolean isWhite;
	/**
     * Material score for the position.
     */
	public int materialScore;

    /**
     * Evaluation score for the white side.
     */

	public int whiteEval;
	/**
     * Evaluation score for the black side.
     */
	public int blackEval;
	
	
	/**
     * Initializes Material Info for the given position.
     *
     * @param board The chessboard for which to perform the evaluation.
     */
	public Evaluation(Board board) {
		isWhite = board.gameStateStack.peek().getIsWhiteToMove();
		
		whiteMaterial = new MaterialInfo(true);
		blackMaterial = new MaterialInfo(false);
		
		mopUpWhite = MopUpEval(true, whiteMaterial.Sum(), blackMaterial.Sum(), whiteMaterial.endgameT);
		mopUpBlack = MopUpEval(false, blackMaterial.Sum(), whiteMaterial.Sum(), blackMaterial.endgameT);

	}
	
	/**
     * End Game incentive to make it easier to checkmate.
     *
     * @param isWhite           Indicates whether the side being evaluated is white.
     * @param myMaterial        Material score for the side being evaluated.
     * @param opponentMaterial  Material score for the opponent side.
     * @param endgameWeight     Weight factor for the endgame.
     * @return The mop-up score for the given conditions.
     */
	public int MopUpEval(boolean isWhite, int myMaterial, int opponentMaterial, float endgameWeight) {
        int mopUpScore = 0;

        if (myMaterial > opponentMaterial + MaterialInfo.PawnValue * 2 && endgameWeight > 0) {
        	
            int friendlyKingSquare = isWhite ? Long.numberOfTrailingZeros(BitBoards.whiteKingBB): Long.numberOfTrailingZeros(BitBoards.blackKingBB);
            int opponentKingSquare = isWhite ? Long.numberOfTrailingZeros(BitBoards.blackKingBB): Long.numberOfTrailingZeros(BitBoards.whiteKingBB);
            
            int opponentKingRank = opponentKingSquare / 8;
            int opponentKingFile = opponentKingSquare % 8;
            
            int friendlyKingRank = friendlyKingSquare / 8;
            int friendlyKingFile = friendlyKingSquare % 8;

            //Incentivize pushing enemy off from center
            int dstFromCentre = Math.max(3 - opponentKingFile, opponentKingFile - 4) + Math.max(3 - opponentKingRank, opponentKingRank - 4);
            
            //Moving King closer to enemey King, cut off routes of escape
            int dstBetweenKings = Math.abs(friendlyKingRank - opponentKingRank) + Math.abs(friendlyKingFile - opponentKingFile);
            
            
            //final score
            mopUpScore = (dstFromCentre) * 10 + (14 - dstBetweenKings) * 10;
            
            if (dstFromCentre > 1) {
            	mopUpScore = (dstFromCentre) * 15 + (14 - dstBetweenKings) * 20;
            }
            
            

            return (int) (mopUpScore * endgameWeight);
        }

        return 0;
    }
	
	/**
     * Performs static evaluation of the current position.
     * The position is assumed to be 'quiet', i.e no captures are available that could drastically affect the evaluation.
     * The score that's returned is given from the perspective of whoever's turn it is to move.
     * So a positive score means the player who's turn it is to move has an advantage, while a negative score indicates a disadvantage.
     *
     * @param isWhite Indicates whether the side being evaluated is white.
     * @return The evaluation score for the current position from the perspective of the side being evaluated.
     */
	public int Evaluate(boolean isWhite) {
		int perspective = isWhite ? 1 : -1;
		
		// Material Score
		int whiteEval = whiteMaterial.Sum();
		int blackEval = blackMaterial.Sum();
		
		//MopUp
		whiteEval += mopUpWhite;
		blackEval += mopUpBlack;
		
		int eval = whiteEval - blackEval;
		return eval * perspective;
	}

}