package gui;

import javax.swing.*;

import game.board.Board;
import game.movegeneration.pieces.PieceI;
import game.search.Move;
import game.search.Searcher;
import game.search.Searcher2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private String[] pieceStrings = {"♟", "♞", "♝", "♜", "♛", "♚"};
	public int depth = 4;
	
	private JPanel[][] panels;
	private JPanel sidePanel;
	public Board chessBoard;

	public GUI(Board chessBoard) {
		this.chessBoard = chessBoard;
		initializeUI();
	}
	public void initializeUI() {
        int rows = 8;
        int cols = 8;

        panels = new JPanel[rows][cols];

        setTitle("Chess GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Use BorderLayout for the main frame

        // Create a panel for the chessboard
        JPanel chessboardPanel = new JPanel(new GridLayout(rows, cols));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                panels[row][col] = new JPanel();
                panels[row][col].setLayout(new BorderLayout());
                panels[row][col].setPreferredSize(new Dimension(80, 80));

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

                chessboardPanel.add(panels[row][col]);
            }
        }

        // Create a panel for additional components
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        
        //Searcher.calcBestMove(chessBoard, depth);
        
        
        // Add your buttons and displays to sidePanel
        // For example:
        
        
        JButton button1 = new JButton("Save Game");
        JButton button2 = new JButton("Load Game");
        
        JLabel label1 = new JLabel("Moves calculated: ");
        JLabel label2 = new JLabel("Current Eval: ");
        JLabel label3 = new JLabel("Current BestMove: ");
        
        label1.setFont(new Font("Serif", Font.PLAIN, 12));
        label2.setFont(new Font("Serif", Font.PLAIN, 12));
        label3.setFont(new Font("Serif", Font.PLAIN, 12));
        
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute when Button 1 is clicked                
                chessBoard.saveGame();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {            	            	         
            	updateBoard();            	
            }
        });

        sidePanel.add(button1);
        sidePanel.add(button2);
        sidePanel.add(label1);
        sidePanel.add(label2);
        sidePanel.add(label3);

        // Add chessboardPanel to the center and sidePanel to the east
        add(chessboardPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

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

	public void updateBoard() {
		PieceI[] boardArray = chessBoard.square;

		int rows = 8;
		int cols = 8;

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int index = row * cols + col;
				PieceI piece = boardArray[index];
				
				String pieceSymbol = (piece != null) ? pieceStrings[piece.getPieceType() -1] : " ";
				int stringColour = (piece != null) ? piece.getPieceColour() : 2;
				JLabel label = new JLabel(pieceSymbol, SwingConstants.CENTER);
				
				if (stringColour != 2) {
					if (stringColour == 0) {
						label.setForeground(Color.WHITE);
					} else {
						label.setForeground(Color.BLACK);
					}	
					label.setFont(new Font("Serif", Font.PLAIN, 50));
					label.setHorizontalAlignment(SwingConstants.CENTER);
				}
				panels[row][col].removeAll(); // Clear the panel
				
				panels[row][col].add(label, BorderLayout.CENTER); // Add label to center
				panels[row][col].revalidate(); // Revalidate the panel
				
				
			}
		}
		updateSidePanel();
		
	}
	
	private void updateSidePanel() {
		Component[] components = sidePanel.getComponents();
		Searcher2 searcher = new Searcher2();
		searcher.calcBestMove(chessBoard, depth);
		
		if (components[2] instanceof JLabel) {
			JLabel label = (JLabel) components[2];
			label.setText("Moves calculated: " + searcher.movesCalculated);
		}
		
		if (components[3] instanceof JLabel) {
			JLabel label = (JLabel) components[3];
			label.setText("Current Eval: " + searcher.bestEvalSoFar);			
		}
		if (components[4] instanceof JLabel) {
			JLabel label = (JLabel) components[4];
			label.setText("Current BestMove: " + Board.translateBBToSquare(searcher.bestMoveSoFar.getFrom()) + " " + Board.translateBBToSquare(searcher.bestMoveSoFar.getTo()));
		}
		
        // Repaint the sidePanel to reflect changes
        sidePanel.revalidate();
        sidePanel.repaint();
        
        chessBoard.printBoard(chessBoard.square);
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