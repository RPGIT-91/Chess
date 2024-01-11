/**
 * Class to manage information about the material in the current chess position.
 * 
 * @author Ryu
 * @version 1.0
 */

package game.search;

import game.movegeneration.BitBoardHelper;
import game.movegeneration.BitBoards;

public class MaterialInfo extends PieceSquareTable{
	private int numPawns;
	private int numBishops;
	private int numQueens;
	private int numRooks;
	private int numKnights;


	public int materialScore;
	public int materialValue;
	public float endgameT;
	
	private int pieceSquareScore;
//	private int pawnScore;
//	private int pawnShieldScore;
	
	public static final int PawnValue = 100;
	public static final int KnightValue = 300;
	public static final int BishopValue = 320;
	public static final int RookValue = 500;
	public static final int QueenValue = 900;
	
	private static final int queenEndgameWeight = 45;
	private static final int rookEndgameWeight = 20;
	private static final int bishopEndgameWeight = 10;
	private static final int knightEndgameWeight = 10;
	

//	private static final int[] passedPawnBonuses = {0, 120, 80, 50, 30, 15, 15};
//	private static final int[] isolatedPawnPenaltyByCount = {0, -10, -25, -50, -75, -75, -75, -75, -75};
//	private static final int[] kingPawnShieldScores = {4, 7, 4, 3, 6, 3};
//
//	private static final float endgameMaterialStart = RookValue * 2 + BishopValue + KnightValue;

	
	/**
     * Creates an instance of MaterialInfo for the specified side.
     *
     * @param isWhite Indicates whether the side is white.
     */
	public MaterialInfo(boolean isWhite) {
		if (isWhite) {
			numPawns = Long.bitCount(BitBoards.whitePawnsBB); //try if it works.
			numKnights = BitBoardHelper.countSetBits(BitBoards.whiteKnightsBB);
			numBishops = BitBoardHelper.countSetBits(BitBoards.whiteBishopsBB);
			numRooks = BitBoardHelper.countSetBits(BitBoards.whiteRooksBB);
			numQueens = BitBoardHelper.countSetBits(BitBoards.whiteQueensBB);
		} else {	
			numPawns = BitBoardHelper.countSetBits(BitBoards.blackPawnsBB);
			numKnights = BitBoardHelper.countSetBits(BitBoards.blackKnightsBB);
			numBishops = BitBoardHelper.countSetBits(BitBoards.blackBishopsBB);
			numRooks = BitBoardHelper.countSetBits(BitBoards.blackRooksBB);
			numQueens = BitBoardHelper.countSetBits(BitBoards.blackQueensBB);
		}

		materialScore = 0;
		materialScore += numPawns * PawnValue;
		materialScore += numKnights * KnightValue;
		materialScore += numBishops * BishopValue;
		materialScore += numRooks * RookValue;
		materialScore += numQueens * QueenValue;
		
		// Endgame Transition (0->1)
		final int endgameStartWeight = 2 * rookEndgameWeight + 2 * bishopEndgameWeight + 2 * knightEndgameWeight + queenEndgameWeight;
		int endgameWeightSum = numQueens * queenEndgameWeight + numRooks * rookEndgameWeight + numBishops * bishopEndgameWeight + numKnights * knightEndgameWeight;
		endgameT = 1 - Math.min(1, endgameWeightSum / (float) endgameStartWeight);
		
		pieceSquareScore = EvaluatePieceSquareTables(isWhite, endgameT);
	}
	
	/**
     * Evaluates the piece square tables for the side in early and late phases.
     *
     * @param isWhite    Indicates whether the side is white.
     * @param endgameT   Endgame transition factor.
     * @return The value based on piece square tables.
     */
	private int EvaluatePieceSquareTables(boolean isWhite, float endgameT) {
		int value = 0;
		int pawnEarly = 0;
		int pawnLate = 0;
		int kingEarlyPhase = 0;
		int kingLatePhase = 0;
		
		//Evaluate pieces and their values
		if (isWhite) {
			value += EvaluatePieceSquareTable(PieceSquareTable.rooks, BitBoards.whiteRooksBB, true);
			value += EvaluatePieceSquareTable(PieceSquareTable.knights, BitBoards.whiteKnightsBB, true);
			value += EvaluatePieceSquareTable(PieceSquareTable.bishops, BitBoards.whiteBishopsBB, true);
			value += EvaluatePieceSquareTable(PieceSquareTable.queens, BitBoards.whiteRooksBB, true);
			
			pawnEarly = EvaluatePieceSquareTable(PieceSquareTable.pawns, BitBoards.whitePawnsBB, true);
			pawnLate = EvaluatePieceSquareTable(PieceSquareTable.pawnsEnd, BitBoards.whitePawnsBB, true);	
			kingEarlyPhase = EvaluatePieceSquareTable(PieceSquareTable.kingStart, BitBoards.whiteKingBB, true);
			kingLatePhase = EvaluatePieceSquareTable(PieceSquareTable.kingEnd, BitBoards.whiteKingBB, true);
		} else {
			value += EvaluatePieceSquareTable(PieceSquareTable.rooks, BitBoards.blackRooksBB, false);
			value += EvaluatePieceSquareTable(PieceSquareTable.knights, BitBoards.blackKnightsBB, false);
			value += EvaluatePieceSquareTable(PieceSquareTable.bishops, BitBoards.blackBishopsBB, false);
			value += EvaluatePieceSquareTable(PieceSquareTable.queens, BitBoards.blackRooksBB, false);

			pawnEarly = EvaluatePieceSquareTable(PieceSquareTable.pawns, BitBoards.blackPawnsBB, false);
			pawnLate = EvaluatePieceSquareTable(PieceSquareTable.pawnsEnd, BitBoards.blackPawnsBB, false);
			kingEarlyPhase = EvaluatePieceSquareTable(PieceSquareTable.kingStart, BitBoards.blackKingBB, false);
			kingLatePhase = EvaluatePieceSquareTable(PieceSquareTable.kingEnd, BitBoards.blackKingBB, false);
		}
		
		//weight pawns and kings differently if endgame
		value += (int) (pawnEarly * (1 - endgameT));
		value += (int) (pawnLate * endgameT);
		value += (int) (kingEarlyPhase * (1 - endgameT));
		value += (int) (kingLatePhase * (endgameT));

		return value;
	}
	
	/**
     * Evaluates a specific piece square table for the side.
     *
     * @param table    The piece square table to evaluate.
     * @param PieceBB  Bitboard representing the pieces on the board.
     * @param isWhite  Indicates whether the side is white.
     * @return The value based on the specified piece square table.
     */
	private int EvaluatePieceSquareTable(int[] table, long PieceBB, boolean isWhite) {
		int value = 0;
		for (int pos : BitBoardHelper.getAllPos(PieceBB)) {
            value += PieceSquareTable.read(table, pos, isWhite);
        }
		
		return value;
	}
	
	/**
     * Calculates the sum of material score and piece square score.
     *
     * @return The sum of material score and piece square score.
     */
	public int Sum() {
		return materialScore + pieceSquareScore;// + pawnScore + pawnShieldScore;
	}

	
}
