package start;
import javax.swing.SwingUtilities;

import game.board.*;
import gui.GUI;

// # Introduction
// Chess Game with move validation
// Move generation and validation is done with set wise bitboards for each piece Type, colour, possible Attacks etc.

// Only valid chess moves are possible. 
// In addition to the normal piece moves that includes: 
// en Passant, en Passant when that when double pinned, not moving out of pins and castling, Checks, double Check, promotion to Queen.
// Not yet, variable pawn promotion.


// The pieces are initialized and displayed on the board. In a sense it functions like a traditional controller.
// This board is returned. Given the BitBoard representation. The GUI also needs to take the shift into account. 
// When a piece is moved, i.e.  "taken" from one square


//Methods to interact with board
//from and to values are based on a bitboard -> 0-63
//each value representing a different square on the board

// A Search negaMax search with a quiescense Search and move ordering finds the best possible move for a given depth and position.
// In the Interface the best move is displayed, as well as the number of searched nodes and the eval score.



// # Board Methods
//board.loadStartPosition() to reset GameBoard
//var board.isWhiteToMove -> true if white piece to move.

public class Main{	
	public static void main(String[] args) {
		Board board = new Board();
		//board.loadFENBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
		
		//board.loadFENBoard("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ");

		//board.loadFENBoard("7k/8/8/8/8/8/8/7K w - - 0 1");
		//board.loadFENBoard("kqp5/ppp1N3/8/8/6K1/2Q5/8/8 w - - 0 1");
//		board.loadFENBoard("kqp5/ppp5/4N3/8/6K1/2Q5/8/8 w - - 0 1");
//		board.loadFENBoard("kq6/p7/PP6/8/8/p7/P7/KQ6 w - - 0 1");
//		board.loadFENBoard("8/3KP3/8/8/8/8/6k1/7q b - - 0 1");
//		board.loadFENBoard("rnbqkbnr/ppp2ppp/3pp3/1N6/Q7/8/PPPPPPPP/R1B1KBNR w KQkq - 0 1");
		
		board.loadFENBoard("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4");
		
		
		//Test Position
		//board.loadFENBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		
		// End Game
//		board.loadFENBoard("8/8/8/4k3/8/8/8/K1R5 w - - 0 1");
		
		
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