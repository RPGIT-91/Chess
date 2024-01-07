package game.search;

import java.util.List;

import game.board.Board;
import game.movegeneration.BitBoardHelper;
import game.movegeneration.BitBoards;

public class MoveOrdering {
    private int[] moveScores;
    private static final int maxMoveCount = 218;

    private static final int squareControlledByOpponentPawnPenalty = 350;
    private static final int capturedPieceValueMultiplier = 10;

    public MoveOrdering(List<Move> moveList) {
        moveScores = new int[maxMoveCount];
    }

    public void orderMoves(Board board, List<Move> moves) {

        for (int i = 0; i < moves.size(); i++) {
            int score = 0;
            Move move = moves.get(i);
            
            int from = move.getFrom();
            int to = move.getTo();
            
            int movePieceType = board.square[Board.toBBSquare(from)].getPieceType();
            int capturePieceType = 0;
            
            if (board.square[Board.toBBSquare(to)] != null) {
            	capturePieceType = board.square[Board.toBBSquare(to)].getPieceType();
            }
            

            if (capturePieceType != 0) {
                // Order moves to try capturing the most valuable opponent piece with least valuable of own pieces first
                // The capturedPieceValueMultiplier is used to make even 'bad' captures like QxP rank above non-captures
                score = capturedPieceValueMultiplier * getPieceValue(capturePieceType) - getPieceValue(movePieceType);
            }

            if (movePieceType == 1) {
            	//if not moving to last rank
                if (to/8 != (1 | 7)) {
                    score += MaterialInfo.QueenValue;
                }
            } else {
                // Penalize moving piece to a square attacked by opponent pawn
            	long pawnAttacks = board.gameStateStack.peek().getIsWhiteToMove() ? BitBoards.blackPawnsAM : BitBoards.whitePawnsAM;
                if (BitBoardHelper.containsSquare(pawnAttacks, to)) {
                    score -= squareControlledByOpponentPawnPenalty;
                }
            }

            moveScores[i] = score;
        }

        sort(moves);
    }

    private static int getPieceValue(int pieceType) {
        switch (pieceType) {
            case 5:
                return MaterialInfo.QueenValue;
            case 4:
                return MaterialInfo.RookValue;
            case 2:
                return MaterialInfo.KnightValue;
            case 3:
                return MaterialInfo.BishopValue;
            case 1:
                return MaterialInfo.PawnValue;
            default:
                return 0;
        }
    }

    private void sort(List<Move> moves) {
        // Sort the moves list based on scores
        for (int i = 0; i < moves.size() - 1; i++) {
            for (int j = i + 1; j > 0; j--) {
                int swapIndex = j - 1;
                if (moveScores[swapIndex] < moveScores[j]) {
                    Move tempMove = moves.get(j);
                    moves.set(j, moves.get(swapIndex));
                    moves.set(swapIndex, tempMove);

                    int tempScore = moveScores[j];
                    moveScores[j] = moveScores[swapIndex];
                    moveScores[swapIndex] = tempScore;
                }
            }
        }
    }
}
