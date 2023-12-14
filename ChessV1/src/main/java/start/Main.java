package start;
import javax.swing.SwingUtilities;

import game.board.*;
import game.search.Searcher;
import gui.GUI;

// # Introduction
// Chess Game with move validation
// Move generation and validation is done with set wise bitboards for each piece Type, colour, possible Attacks etc.

// Only valid chess moves are possible. 
// In addition to the normal piece moves that includes: 
// en Passant, not moving out of pins and castling, Checks, double Check, promotion to Queen.
// Not yet variable pawn promotion.

// en Passant when that would result in a check.


// The pieces are initialized and displayed on the board. In a sense it functions like a traditional controller.
// This board is returned. Given the BitBoard representation. The GUI also needs to take the shift into account. 
// When a piece is moved, i.e.  "taken" from one square

//
//Methods to interact with board
//from and to values are based on a bitboard -> 0-63
//each value representing a different square on the board



// # Board Methods
//board.loadStartPosition() to reset GameBoard
//var board.isWhiteToMove -> true if white piece to move.

public class Main{	
	public static void main(String[] args) {
		Board board = new Board();
		
		Searcher.makeAllMoves(board);
//		board.movePiece(12, 28);
//		board.movePiece(55, 39);
//		board.movePiece(4, 12);
//		board.loadPreviousBoard();
//		board.loadPreviousBoard();
		
		
		
//		for (String i : board.fenStack){
//			System.out.println(i);
//		}
		//board.LoadPositionFromFEN("rnb2bnr/pp1p1kpp/5p2/q3pP1K/2p5/6P1/PPPPP2P/RNBQ1BNR b  - 0 7", true);
		

		
		//System.out.println(FEN.currentFen(board));
		//Board.printAllBB(true);
//		board.movePiece(55, 39);
//		board.movePiece(4, 12);
//		board.movePiece(39, 31);
//		board.movePiece(12, 20);
//		board.movePiece(63, 39);
//		board.movePiece(28, 36);
//		board.movePiece(39, 38);
//		board.movePiece(20, 28);
//		board.movePiece(38, 39);
//		board.movePiece(28, 35);
		
		
//		board.movePiece(48, 32);
//		board.movePiece(4, 12);
//		board.movePiece(32, 24);
//		board.movePiece(12, 20);
//		board.movePiece(24, 16);
//		board.movePiece(20, 29);
//		board.movePiece(56, 24);
//		board.movePiece(9, 17);
//		board.movePiece(50, 34);
//		board.movePiece(2, 9);
//		board.movePiece(34, 26);
//		board.movePiece(28, 36);
		
		// Game Start
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI(board);
            }
		});
		//debugging 
		//Board.printAllBB(true);
		//Board.printBitBoard(BitBoards.allBB, true);
		//Board.printBitBoard(square[1].generateSamePieceAttacks(isWhiteToMove));
	}

}