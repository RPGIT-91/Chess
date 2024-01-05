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
// en Passant, en Passant when that when double pinned, not moving out of pins and castling, Checks, double Check, promotion to Queen.
// Not yet variable pawn promotion.



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
		//board.loadFENBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
		
		//board.loadFENBoard("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ");

		//board.loadFENBoard("7k/8/8/8/8/8/8/7K w - - 0 1");
		//board.loadFENBoard("rkb5/pp6/8/1P6/8/8/8/K4B2 b - - 0 1");
		//board.loadFENBoard("knb5/p2p4/1p6/pP1P4/PpBp4/1PpP4/B1P5/KB6 b - - 0 1");
		
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