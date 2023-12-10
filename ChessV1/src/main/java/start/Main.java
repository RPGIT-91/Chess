package start;
import javax.swing.SwingUtilities;

import game.board.*;
import gui.GUI;

// # Introduction
// Chess Game with move validation
// Move generation and validation is done with set wise bitboards for each piece Type, colour, possible Attacks etc.

// Only valid chess moves are possible. 
// In addition to the normal piece moves that includes: en Passant, not moving out of pins and castling, Checks, double Check.
// Not yet, Pawn promotion.


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
//		
//		board.movePiece(11, 27);
//		board.movePiece(51, 35);
//		
//		board.movePiece(2, 29);
//		board.movePiece(58, 37);
//		
//		board.movePiece(3, 19);
//		board.movePiece(59, 43);
//		
//		board.movePiece(1, 18);
//		board.movePiece(57, 42);
//		
//		board.movePiece(12, 28);
//		board.movePiece(52, 36);
//		
//		board.movePiece(6, 21);
//		board.movePiece(62, 45);
//		
//		board.movePiece(5, 12);
//		board.movePiece(61, 52);

		
		// Game Start
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI(board);
            }
		});

		//debugging 
		Board.printAllBB(true);
		//Board.printBitBoard(BitBoards.allBB, true);
		//Board.printBitBoard(square[1].generateSamePieceAttacks(isWhiteToMove));
	}

}