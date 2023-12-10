package gui;

import javax.swing.*;

import game.board.board.Board;
import game.movegeneration.pieces.PieceI;

import javax.swing.*;
import game.board.board.Board;
import game.movegeneration.pieces.PieceI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
	private JPanel[][] panels;
	public Board chessBoard;

	public GUI(Board chessBoard) {
		this.chessBoard = chessBoard;
		initializeUI();
	}
	public void initializeUI() {
		int rows = 8;
		int cols = 8;

		panels = new JPanel[rows][cols]; // Use JPanel instead of JButton

		setTitle("Chess GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(rows, cols));

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				panels[row][col] = new JPanel(); // Use JPanel instead of JButton
				panels[row][col].setLayout(new BorderLayout());
				panels[row][col].setPreferredSize(new Dimension(80, 80)); // Set the preferred size

				// Set background color based on row and column
				if (((row + col) % 2) != 0) {
					panels[row][col].setBackground(Color.decode("#e9d5c4"));
				} else {
					panels[row][col].setBackground(Color.decode("#d2ad8e"));
				}

				final int currentRow = row;
				final int currentCol = col;

				panels[row][col].addMouseListener(new java.awt.event.MouseAdapter() {
					public void mousePressed(java.awt.event.MouseEvent evt) {
						handlePanelClick(currentRow, currentCol, evt);
					}
				});

				add(panels[row][col]);
			}
		}

		updateBoard();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}


	private int selectedRow = -1;
	private int selectedCol = -1;
	private boolean selectingMode = true;

	private void handlePanelClick(int row, int col, java.awt.event.MouseEvent evt) {
		if (selectingMode) {
			// First click - highlight possible moves
			showValidMoves(chessBoard.showValidMoves(toBBSquare(row, col)));


			selectedRow = row;
			selectedCol = col;
			selectingMode = false;

		} else {
			 // Second click
			long currentValidMoves = chessBoard.showValidMoves(toBBSquare(row, col));
	        long validMovesBitboard = chessBoard.showValidMoves(toBBSquare(selectedRow, selectedCol));
//	        chessBoard.printBitBoard((1L << (inverseRowAndCol(row) * 8 + col)), false);
	        if ((validMovesBitboard & (1L << toBBSquare(row, col))) != 0) {
	            // Second click on a valid move - move the piece
	            chessBoard.movePiece(toBBSquare(selectedRow, selectedCol), toBBSquare(row, col));
	            resetSelection();
	            updateBoard();
	            selectingMode = true;
	        } else {
	            // Second click on a different square - show valid moves for that square
	            showValidMoves(currentValidMoves);
	            selectedRow = row;
	            selectedCol = col;
	            selectingMode = false;
	        }
			// Second click - move the piece
		}
	}


	private void showValidMoves(long validMovesBitboard) {
		SwingUtilities.invokeLater(() -> {
			for (int rank = 7; rank >= 0; rank--) {
				for (int file = 0; file < 8; file++) {
					int square = rank * 8 + file;
					long mask = 1L << square;

					//System.out.println(index);

					// Check if the corresponding bit is set in the validMovesBitboard
					if ((validMovesBitboard & mask) != 0) {
						if ((rank + file) % 2 != 0){
							panels[inverseRowAndCol(rank)][file].setBackground(Color.decode("#B6DFA4"));
						} else {
							panels[inverseRowAndCol(rank)][file].setBackground(Color.decode("#b1daa0"));
						}

					} else {
						panels[inverseRowAndCol(rank)][file].setBackground(getSquareColor(rank, file)); // Reset the background color
					}
				}
			}
		});
	}

	private void resetSelection() {
		selectedRow = -1;
		selectedCol = -1;
	}

	private void updateBoard() {
		PieceI[] boardArray = chessBoard.square;

		int rows = 8;
		int cols = 8;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int index = row * cols + col;
				PieceI piece = boardArray[index];
				String pieceSymbol = (piece != null) ? Integer.toString(piece.getPieceType()) : " "; // Adjust based on
				// your PieceI
				// class

				panels[row][col].removeAll(); // Clear the panel
				JLabel label = new JLabel(pieceSymbol, SwingConstants.CENTER);
				panels[row][col].add(label, BorderLayout.CENTER); // Add label to center
				panels[row][col].revalidate(); // Revalidate the panel
			}
		}
	}

	private Color getSquareColor(int rank, int file) {
		if ((rank + file) % 2 != 0) {
			return Color.decode("#e9d5c4");
		} else {
			return Color.decode("#d2ad8e");
		}
	}

	//Helper
	private static int inverseRowAndCol(int row) {
		int bbRow = 0;
		switch (row) {
		case 0:
			bbRow = 7;
			break;
		case 1:
			bbRow = 6;
			break;
		case 2: 
			bbRow = 5;
			break;
		case 3:
			bbRow = 4;
			break;
		case 4: 
			bbRow = 3;
			break;
		case 5:
			bbRow = 2;
			break;
		case 6:
			bbRow = 1;
			break;
		case 7:
			bbRow = 0;
			break;
		}
		return bbRow;
	}

	private static int toBBSquare(int row, int col) {
		int bbPos = inverseRowAndCol(row) * 8 + col;
		return bbPos;
	}
}