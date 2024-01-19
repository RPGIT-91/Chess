package gui;

import javax.swing.*;

import game.board.Board;
import game.movegeneration.pieces.PieceI;
import game.search.Searcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The GUI class represents the graphical user interface for a chess game.
 * It extends JFrame and provides a chessboard, side panel with buttons,
 * and labels to display game information.
 * 
 * @see game.board.Board
 * @see game.search.Searcher
 * 
 * @author Ryu
 * @version 1.0
 */
public class GUI extends JFrame implements GameObserver{
	private static final long serialVersionUID = 1L;
	private String[] pieceStrings = {"♟", "♞", "♝", "♜", "♛", "♚"};
	public int depth = 4;

	private JPanel[][] panels;
	private JPanel sidePanel;
	public Board chessBoard;

	private BotSetting botSettings;

	private int selectedRow = -1;
	private int selectedCol = -1;
	private boolean selectingMode = true;
	/**
	 * Constructs a GUI object for a chess game.
	 *
	 * @param chessBoard The chessboard associated with the GUI.
	 */
	public GUI(Board chessBoard, BotSetting botS) {
		this.chessBoard = chessBoard;

		this.botSettings = botS;
		botSettings.addObserver(this);
		initializeUI();
	}

	/**
	 * Initializes the graphical user interface with the chessboard,
	 * side panel, buttons, and labels.
	 */
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
		JButton button3 = new JButton("New Game");

		JLabel label1 = new JLabel("Moves calculated: ");
		JLabel label2 = new JLabel("Current Eval: ");
		JLabel label3 = new JLabel("Current BestMove: ");

		label1.setFont(new Font("Serif", Font.PLAIN, 12));
		label2.setFont(new Font("Serif", Font.PLAIN, 12));
		label3.setFont(new Font("Serif", Font.PLAIN, 12));


		/**
		 * Saves the current game state to a file named "SavedGame.txt" in the project directory.
		 * The method writes the FEN string of the current game state to the file.
		 * Displays a success message if the save is successful, otherwise prints an error message.
		 */
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Code to execute when Button 1 is clicked   
				try {
					// Create a FileChooser to get the user-selected file
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Save Game");
					// Set the initial directory to be within your project
					String projectPath = System.getProperty("user.dir");
					String initialFolderPath = projectPath + File.separator + "SavedGames";
			        fileChooser.setCurrentDirectory(new File(initialFolderPath));
			        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
			        fileChooser.setFileFilter(filter);

					int userSelection = fileChooser.showSaveDialog(null);

					if (userSelection == JFileChooser.APPROVE_OPTION) {
						File fileToSave = fileChooser.getSelectedFile();

						// Ensure the file has the .txt extension
		                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
		                    fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
		                }
						// Write the serialized Board object to the selected file
						try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
							String fenValue = chessBoard.fenStack.peek();
							writer.write(fenValue);
							System.out.println("Game saved successfully to " + fileToSave);
						} catch (IOException f) {
							System.err.println("Error writing to file: " + f.getMessage());
						}		              
					}
				} catch (Exception g) {
					System.err.println("Error saving game: " + g.getMessage());
				}

			}
		});

		/**
		 * loads a previously saved game from a file named by the user 
		 * The method imports the FEN string of the saved game from the file into the game state 
		 * Displays a success message if the import is successful, otherwise prints an error message.
		 */
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				try {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Load Game");
					// Set the initial directory to be within your project
					String projectPath = System.getProperty("user.dir");
					String initialFolderPath = projectPath + File.separator + "SavedGames";
			        fileChooser.setCurrentDirectory(new File(initialFolderPath));
			        
			        // Restrict file types to .txt
			        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
			        fileChooser.setFileFilter(filter);
					int userSelection = fileChooser.showOpenDialog(null);

					if (userSelection == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();

						try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
							String fenValue = reader.readLine();
							chessBoard.loadFENBoard(fenValue);
							System.out.println("Game loaded successfully from " + selectedFile);
						} catch (IOException f) {
							System.err.println("Error loading game: " + f.getMessage());
						}
					}
				} catch (Exception g) {
					System.err.println("Error loading game: " + g.getMessage());
				}

				updateBoard();            	
			}
		});

		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chessBoard.loadStartPosition();
				updateBoard();
			}
		});

		sidePanel.add(button1);
		sidePanel.add(button2);
		sidePanel.add(button3);
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



	/**
	 * Handles the click event on a chessboard panel. Manages piece selection
	 * and move execution based on user input.
	 *
	 * @param row The row index of the clicked panel.
	 * @param col The column index of the clicked panel.
	 * @param evt The mouse event associated with the click.
	 */
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
				// Notify the observer (GUI) that a human move is made
				update(null, null);
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

	/**
	 * Displays the valid moves on the chessboard panels with different colors.
	 *
	 * @param validMovesBitboard The bitboard representing valid moves.
	 */
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

	/**
	 * Resets the selection of a chessboard panel.
	 */
	private void resetSelection() {
		selectedRow = -1;
		selectedCol = -1;
	}

	/**
	 * Updates the graphical representation of the chessboard.
	 */
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

	/**
	 * Updates the side panel with information about moves calculated,
	 * current evaluation, and the best move.
	 */
	private void updateSidePanel() {
		Component[] components = sidePanel.getComponents();
		Searcher searcher = new Searcher();
		searcher.calcBestMove(chessBoard, depth);

		BotSetting.setNextFrom(searcher.bestMoveSoFar.getFrom());
		BotSetting.setNextTo(searcher.bestMoveSoFar.getTo());

		if (components[3] instanceof JLabel) {
			JLabel label = (JLabel) components[3];
			label.setText("Moves calculated: " + searcher.movesCalculated);
		}

		if (components[4] instanceof JLabel) {
			JLabel label = (JLabel) components[4];
			label.setText("Current Eval: " + searcher.bestEvalSoFar);			
		}
		if (components[5] instanceof JLabel) {
			JLabel label = (JLabel) components[5];
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

	// ### Helper

	/**
	 * Represents the inverse mapping of the row index from the chessboard
	 * to the corresponding row index in the bitboard representation.
	 *
	 * @param row The row index from the chessboard.
	 * @return The corresponding row index in the bitboard representation.
	 */
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

	/**
	 * Converts row and column indices to the bitboard position.
	 *
	 * @param row The row index of the chessboard.
	 * @param col The column index of the chessboard.
	 * @return The bitboard position corresponding to the row and column indices.
	 */
	private static int toBBSquare(int row, int col) {
		int bbPos = inverseRowAndCol(row) * 8 + col;
		return bbPos;
	}



	@Override
	public void update(Observable o, Object arg) {

		// Introduce a delay before making the bot move
		Timer timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeBotMove();
			}
		});
		timer.setRepeats(false);
		timer.start();

	}

	@Override
	public void makeBotMove() {


		chessBoard.movePiece(BotSetting.getNextFrom(), BotSetting.getNextTo());
		updateBoard();
		update(null, null);

	}
}