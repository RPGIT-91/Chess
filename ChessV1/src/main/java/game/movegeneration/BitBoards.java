package game.movegeneration;

import java.util.ArrayList;
import java.util.List;

import game.movegeneration.pieces.*;

/**
 * The BitBoards class represents a collection of bitboards for various chess pieces and their positions.
 * It includes methods for updating these bitboards based on chess moves and checking for certain conditions,
 * such as whether a king is in check or generating legal moves for a piece.
 * <p>
 * The class also provides methods for handling different types of pins (diagonal, orthogonal) and checks
 * on the chessboard.
 * </p>
 * <p>
 * Note: This class assumes a standard 8x8 chessboard representation.
 * </p>
 *
 * @see game.movegeneration.BitBoardHelper
 * 
 * @author Ryu
 * @version 1.0
 */
public class BitBoards extends BitBoardHelper{
	// Pieces
	public static long pawnsBB;
	public static long bishopsBB;
	public static long knightsBB;
	public static long rooksBB;
	public static long queensBB;
	public static long kingsBB;
	//Colour
	public static long whiteBB;
	public static long blackBB;
	public static long allBB = whiteBB | blackBB;
	//Piece and colour
	public static long whitePawnsBB = pawnsBB & whiteBB;
	public static long whiteKnightsBB = knightsBB & whiteBB;;
	public static long whiteBishopsBB = bishopsBB & whiteBB;
	public static long whiteRooksBB = rooksBB & whiteBB;;
	public static long whiteQueensBB = queensBB & whiteBB;;
	public static long whiteKingBB = kingsBB & whiteBB;;

	public static long blackPawnsBB = pawnsBB & blackBB;
	public static long blackKnightsBB = knightsBB & blackBB;
	public static long blackBishopsBB = bishopsBB & blackBB;
	public static long blackRooksBB = rooksBB & blackBB;
	public static long blackQueensBB = queensBB & blackBB;
	public static long blackKingBB = kingsBB & blackBB;
	//Attack Masks
	public static long whiteAM;
	public static long blackAM;

	public static long whitePawnsAM;
	public static long whiteBishopsAM;
	public static long whiteKnightsAM;
	public static long whiteRooksAM;
	public static long whiteQueensAM;
	public static long whiteKingAM;
	public static long blackPawnsAM;
	public static long blackBishopsAM;
	public static long blackKnightsAM;
	public static long blackRooksAM;
	public static long blackQueensAM;
	public static long blackKingAM;

	/**
	 * Array representing bishop moves for move generation.
	 */
	private static final int[] bishopMoves = {-9, -7, 7, 9};
	/**
	 * Array representing rook moves for move generation.
	 */
	private static final int[] rookMoves = {-8, -1, 1, 8};

	
	/**
     * Adds a piece to the specified position on the chessboard.
     *
     * @param pos         The position on the chessboard (0 to 63).
     * @param pieceType   The type of the chess piece (1 for pawn, 2 for knight, etc.).
     * @param pieceColour The color of the chess piece (0 for white, 1 for black).
     */
	public static void add(int pos, int pieceType, int pieceColour) {

		//check if already set elsewhere and wether we are not adding a piece.none.
		if ((allBB & (1L << pos)) == 0  && (pieceType + pieceColour) > 0) {
			switch (pieceType){
			case 1:
				pawnsBB = setSquare(pawnsBB, pos);
				break;
			case 2:
				knightsBB = setSquare(knightsBB, pos);
				break;
			case 3:
				bishopsBB = setSquare(bishopsBB, pos);
				break;
			case 4:
				rooksBB = setSquare(rooksBB, pos);
				break;
			case 5:
				queensBB = setSquare(queensBB, pos);
				break;
			case 6:
				kingsBB = setSquare(kingsBB, pos);
				break;
			default:
				break;
			}

			switch (pieceColour) {
			case 0:
				whiteBB = setSquare(whiteBB, pos);
				break;
			case 1:
				blackBB = setSquare(blackBB, pos);
				break;
			default:
				break;
			}

			updateAll();
		}
		else {
			System.out.println("Square already occupied");
		}
	}

	/**
     * Clears the bitboards for a specific position on the chessboard.
     *
     * @param pos The position on the chessboard (0 to 63).
     *            Note: This method is currently not used.
     */
	public static void clear(int pos) {
		allBB = clearSquare(allBB, pos);

		blackBB = blackBB & allBB;
		whiteBB = whiteBB & allBB;

		pawnsBB = pawnsBB & allBB;
		knightsBB = knightsBB & allBB;
		bishopsBB = bishopsBB & allBB;
		rooksBB = rooksBB & allBB;
		queensBB = queensBB & allBB;
		kingsBB = kingsBB & allBB;

		updateAll();
	}
	
	/**
	 * generates orthogonal Slider Moves
	 * 
	 * @param from The position of the piece to be moved.
	 * @param isWhite Indicates whether the moving piece is white.
	 * @param possibleMoves a bitboard that contains previously calculated possibleMoves.
	 */
	public static long generateOrthogonalSlider(long position, boolean isWhite, long possibleMoves, boolean attacks) {
		for (int move : rookMoves) {
			long newPosition = position;

			// Generate rook moves
			while (true) {
				int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
				int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

				newPosition = (newPosition << move | newPosition >>> -move);

				// Check if the new position is outside the board or on a different rank/file
				int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
				int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

				if (Math.abs(newRank - oldRank) == 7 | Math.abs(newFile - oldFile) == 7) {
					break;
				}

				// Check if the new square is unoccupied or occupied by an opponent's piece
				if ((newPosition & BitBoards.allBB) == 0) {
					possibleMoves |= newPosition;
				} else {
					// checking for attacksMask?
					if (attacks) {
						//stop sliding
						possibleMoves |= newPosition;
						break;
					} else {
						// If the square is occupied by an opponent's piece, include the capture move and stop sliding
						if ((isWhite && (newPosition & BitBoards.blackBB) != 0) || (!isWhite && (newPosition & BitBoards.whiteBB) != 0)) {
							possibleMoves |= newPosition;
						}
						break;
					}
				}
			}
		}
		return possibleMoves;
	}

	/**
	 * generates diagonal slider moves
	 * 
	 * @param from The position of the piece to be moved.
	 * @param isWhite Indicates whether the moving piece is white.
	 * @param possibleMoves a bitboard that contains previously calculated possibleMoves.
	 */
	public static long generateDiagonalSlider(long position, boolean isWhite, long possibleMoves, boolean attacks) {
		for (int move : bishopMoves) {
			long newPosition = position;

			// Generate diagonal moves
			while (true) {	        	
				int oldRank = Long.numberOfTrailingZeros(newPosition) / 8;
				int oldFile = Long.numberOfTrailingZeros(newPosition) % 8;

				newPosition = (newPosition << move) | (newPosition >>> -move);

				// Check if the new position is outside the board or on a different diagonal
				int newRank = Long.numberOfTrailingZeros(newPosition) / 8;
				int newFile = Long.numberOfTrailingZeros(newPosition) % 8;

				if (Math.abs(newRank - oldRank) == 7 | Math.abs(newRank-oldRank) == 0 | Math.abs(newFile - oldFile) == 7) {
					break;
				}
				// Check if the new square is unoccupied or occupied by an opponent's piece
				if ((newPosition & BitBoards.allBB) == 0) {
					possibleMoves |= newPosition;
				} else {
					// checking for attacksMask?
					if (attacks) {
						//stop sliding
						possibleMoves |= newPosition;
						break;
					} else {
						// If the square is occupied by an opponent's piece, include the capture move and stop sliding
						if ((isWhite && (newPosition & BitBoards.blackBB) != 0) || (!isWhite && (newPosition & BitBoards.whiteBB) != 0)) {
							possibleMoves |= newPosition;
						}
						break;
					}
				}
			}
		}
		return possibleMoves;
	}

	/**
     * Checks if the king of the specified color is in a double-check position.
     *
     * @param isWhite Indicates whether the king being checked is white.
     * @return True if the king is in a double-check position; false otherwise.
     */
	public static boolean doubleCheck(boolean isWhite) {
		int checks = 0;
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);

		long enemyPawns = (!isWhite ? whitePawnsAM:blackPawnsAM);
		long enemyKnights = (!isWhite ? whiteKnightsAM:blackKnightsAM);
		long enemyBishops = (!isWhite ? whiteBishopsAM:blackBishopsAM);
		long enemyRooks = (!isWhite ? whiteRooksAM:blackRooksAM);
		long enemyQueens = (!isWhite ? whiteQueensAM:blackQueensAM);

		if ((kingSquare & enemyPawns) != 0) {
			checks += 1;
		}
		if ((kingSquare & enemyKnights) != 0) {
			checks += 1;
		}
		if ((kingSquare & enemyBishops) != 0) {
			checks += 1;
		}
		if ((kingSquare & enemyRooks) != 0) {
			checks += 1;
		}
		if ((kingSquare & enemyQueens) != 0) {
			checks += 1;
		}
		return (checks < 2);
	}

	/**
     * Checks if the king of the specified color is in a single-check position.
     *
     * @param isWhite Indicates whether the king being checked is white.
     * @return A bitboard representing possible moves that resolve the single check.
     *         Note: This method is specific to the color of the checked king.
     */
	public static long singleCheck(boolean isWhite) {
		// Step 1: find out if king attacked
		// Step 2: find out by which piece
		// Step 3: pawn and knight --> capture checking piece
		// Step 4: other pieces --> block or capture checking piece

		long possibleMoves = 0L;
		long enemyAttacks = (!isWhite ? whiteAM : blackAM);
		long kingSquare = (isWhite ? whiteKingBB : blackKingBB);
		int  kingpos = Long.numberOfTrailingZeros(kingSquare);

		if ((enemyAttacks & kingSquare) != 0) {
			long enemyPawns = (!isWhite ? whitePawnsAM:blackPawnsAM);
			long enemyKnights = (!isWhite ? whiteKnightsAM:blackKnightsAM);
			long enemyBishops = (!isWhite ? whiteBishopsAM:blackBishopsAM);
			long enemyRooks = (!isWhite ? whiteRooksAM:blackRooksAM);
			long enemyQueens = (!isWhite ? whiteQueensAM:blackQueensAM);

			long horizontal = (0xFFL << ((kingpos / 8) * 8));
			long vertical = (0x0101010101010101L << (kingpos % 8));
			long diagonalPos = generatePositiveDiagonal(kingpos);
			//0x8040201008040201L >>> A +1;
			long diagonalNeg = generateNegativeDiagonal(kingpos);


			// PAWN
			if ((kingSquare & enemyPawns) != 0) {
				possibleMoves |= (!isWhite ? whitePawnsBB:blackPawnsBB)  & diagonalPos;
				possibleMoves |= (!isWhite ? whitePawnsBB:blackPawnsBB)  & diagonalNeg;
			}
			//KNIGHT
			if ((kingSquare & enemyKnights) != 0) {
				possibleMoves |= (!isWhite ? whiteKnightsBB:blackKnightsBB) & Knight.generate5x5SquareMask(kingSquare);
			}


			//SLIDERS
			if ((kingSquare & enemyBishops) != 0) {
				if (((!isWhite ? whiteBishopsBB:blackBishopsBB) & diagonalPos) != 0) {
					long attackingPiece = (!isWhite ? whiteBishopsBB:blackBishopsBB) & diagonalPos;					
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= diagonalPos;
					possibleMoves |= inBetween;
				} else if (((!isWhite ? whiteBishopsBB:blackBishopsBB) & diagonalNeg) != 0) {
					long attackingPiece = (!isWhite ? whiteBishopsBB:blackBishopsBB) & diagonalNeg;					
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= diagonalNeg;
					possibleMoves |= inBetween;
				}
			}
			if ((kingSquare & enemyRooks) != 0) {				
				if (((!isWhite ? whiteRooksBB:blackRooksBB) & horizontal) != 0) {
					long attackingPiece = (!isWhite ? whiteRooksBB:blackRooksBB) & horizontal;					
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= horizontal;
					possibleMoves |= inBetween;
				} else if (((!isWhite ? whiteRooksBB:blackRooksBB) & vertical) != 0) {	
					long attackingPiece = (!isWhite ? whiteRooksBB:blackRooksBB) & vertical;
					long inBetween = bitsBetween(kingSquare, attackingPiece);					
					inBetween &= vertical;
					possibleMoves |= inBetween;
				}
			}
			if ((kingSquare & enemyQueens) != 0) {
				if (((!isWhite ? whiteQueensBB:blackQueensBB) & diagonalPos) != 0) {
					long attackingPiece = (!isWhite ? whiteQueensBB:blackQueensBB) & diagonalPos;	
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= diagonalPos;			
					possibleMoves |= inBetween;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & diagonalNeg) != 0) {		
					long attackingPiece = (!isWhite ? whiteQueensBB:blackQueensBB) & diagonalNeg;	
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= diagonalNeg;			
					possibleMoves |= inBetween;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & horizontal) != 0) {
					long attackingPiece = (!isWhite ? whiteQueensBB:blackQueensBB) & horizontal;	
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= horizontal;			
					possibleMoves |= inBetween;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & vertical) != 0) {
					long attackingPiece = (!isWhite ? whiteQueensBB:blackQueensBB) & vertical;	
					long inBetween = bitsBetween(kingSquare, attackingPiece);
					inBetween &= vertical;			
					possibleMoves |= inBetween;
				}
			}
		}	
		return possibleMoves;
		// this bitboard needs to be checked against the current moves.
	}

	/**
     * Checks for possible orthogonal moves that avoid breaking a pin.
     *
     * @param from    The position of the piece to be moved.
     * @param isWhite Indicates whether the moving piece is white.
     * @return A bitboard with possible moves that don't break an orthogonal pin.
     */
	public static long checkOrthogonalPin(int from, boolean isWhite) {	
		// Step 1: piece to move is attacked by enemy bishop, rook, queen. 
		// Step 2: Find out which direction
		// Step 3: check if array is onto king square.
		// Step 4: check if that array is the one from the attacking piece, only relevant for queen.
		// Step 5: check if array is blocked before
		// return bitboard with moves that don't break the pin
		int file = from % 8;
		int rank = from / 8;

		long sliderAM = (!isWhite ? whiteRooksAM | whiteQueensAM : blackRooksAM | blackQueensAM);
		long sliderSquare = (!isWhite ? whiteQueensBB | whiteRooksBB : blackQueensBB | blackRooksBB);
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);
		long movingPiece = 1L << from;
		long blocked = allBB & ~kingSquare;
		long possibleRay = ~0L;

		// Step 1
		if ((sliderAM & movingPiece) != 0) {
			possibleRay = 0L;
			//Step 2
			// Generate horizontal ray bitboard, w/o the current square
			long horizontal = (0xFFL << (((rank) * 8) & ~(1L << from)));
			long vertical = (0x0101010101010101L << (file) & ~(1L << from));


			// Step 3
			if ((kingSquare & horizontal) != 0) {
				possibleRay |= horizontal;

			}

			if ((kingSquare & vertical) != 0) {
				possibleRay |= vertical;

			}

			//Board.printBitBoard(possibleRay, false);

			// Step 4
			if ((possibleRay & sliderSquare) == 0) {
				possibleRay = ~0L;
			}

			//Step 5
			long inBetween = bitsBetween(kingSquare, movingPiece);
			inBetween &= ~(kingSquare | movingPiece);
			inBetween &= possibleRay;

			//Board.printBitBoard(inBetween, false);
			// If there is a piece between moving piece and king all moves are possible
			if ((inBetween & blocked) != 0) {
				possibleRay = ~0L;
			}
		}
		//Check this long against the current possibleMoves -> possibleMoves & possibleRay = legal moves.
		return possibleRay;
	}

	/**
     * Checks for possible diagonal moves that avoid breaking a pin.
     *
     * @param from    The position of the piece to be moved.
     * @param isWhite Indicates whether the moving piece is white.
     * @return A bitboard with possible moves that don't break a diagonal pin.
     */
	public static long checkDiagonalPin(int from, boolean isWhite){
		long sliderAM = (!isWhite ? whiteBishopsAM | whiteQueensAM : blackBishopsAM | blackQueensAM);
		long sliderSquare = (!isWhite ? whiteQueensBB | whiteBishopsBB : blackQueensBB | blackBishopsBB);
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);
		long movingPiece = 1L << from;
		long blocked = allBB & ~kingSquare;
		long possibleRay = ~0L;
		
		// Step 1
		if ((sliderAM & movingPiece) != 0) {
			possibleRay = 0L;
			
			//Step 2
			long diagonalPos = generatePositiveDiagonal(from) & ~(1L << from);
			long diagonalNeg = generateNegativeDiagonal(from) & ~(1L << from);
			
			// Step 3
			if ((kingSquare & diagonalPos) != 0) {
				possibleRay |= diagonalPos;
			}
			
			if ((kingSquare & diagonalNeg) != 0) {
				possibleRay |= diagonalNeg;
			}

			//Board.printBitBoard(possibleRay, false);

			// Step 4
			if ((possibleRay & sliderSquare) == 0) {
				possibleRay = ~0L;
			}

			//Step 5
			long inBetween = bitsBetween(kingSquare, movingPiece);
			inBetween &= ~(kingSquare | movingPiece);
			inBetween &= possibleRay;

			//Board.printBitBoard(inBetween, false);
			// If there is a piece between moving piece and king all moves are possible
			if ((inBetween & blocked) != 0) {
				possibleRay = ~0L;
			}
		}
		//Check this long against the current possibleMoves -> possibleMoves & possibleRay = legal moves.
		return possibleRay;

	}

	/**
     * Checks for possible en passant moves that avoid breaking a pin.
     *
     * @param from    The position of the moving pawn.
     * @param isWhite Indicates whether the moving pawn is white.
     * @param epFile  The file of the en passant capture square.
     * @return True if en passant is possible without breaking a pin; false otherwise.
     */
	public static boolean checkEnPassantPin(int from, boolean isWhite, int epFile) {	
		// Step 1: piece to move or EnPassant capture square is attacked by enemy rook, bishop, queen. 
		// Step 2: Find out which direction
		// Step 3: check if array is onto king square.
		// Step 4: check if that array is the one from the attacking piece, only relevant for queen.
		// Step 5: check if array is blocked before
		// return bitboard with moves that don't break the pin
		int file = from % 8;
		int rank = from / 8;

		long sliderAM = (!isWhite ? whiteRooksAM | whiteQueensAM : blackRooksAM | blackQueensAM);
		long sliderSquare = (!isWhite ? whiteQueensBB | whiteRooksBB | whiteBishopsBB : blackQueensBB | blackRooksBB | blackBishopsBB);
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);
		long movingPawn = 1L << from;
		long blocked = whiteBB | blackBB;
		long possibleRay = ~0L;
		boolean possible = true;

		long extendedPawn = movingPawn << 1 | movingPawn >> 1;

		// Step 1
		if ((sliderAM & extendedPawn) != 0) {
			//Step 2
			// Generate horizontal ray bitboard, w/o the current square
			long horizontal = (0xFFL << (((rank) * 8) & ~(1L << from)));
			long vertical = (0x0101010101010101L << (file) & ~(1L << from));
			long diagonalPos = generatePositiveDiagonal(from) & ~(1L << from);
			long diagonalNeg = generateNegativeDiagonal(from) & ~(1L << from);

			// Step 3
			if ((kingSquare & horizontal) != 0) {
				possibleRay = horizontal;

			} else if ((kingSquare & vertical) != 0) {
				possibleRay = vertical;
			} else if ((kingSquare & diagonalPos) != 0) {
				possibleRay = diagonalPos;

			} else if ((kingSquare & diagonalNeg) != 0) {
				possibleRay = diagonalNeg;
			}

			// Step 4
			if ((possibleRay & sliderSquare) == 0) {
				possibleRay = ~0L;
			}

			//Step 5
			long verticalEP = (0x0101010101010101L << (epFile));
			long inBetween = bitsBetween(kingSquare, movingPawn >> 1);		
			inBetween &= ~(kingSquare | movingPawn | verticalEP);
			inBetween &= possibleRay;




			blocked &= possibleRay;

			// If there is more than a single piece between moving piece and king en passant is still possible
			if ((inBetween & blocked) == 0) {

				possible = false;

			}
		}

		//Check this long against the current possibleMoves -> possibleMoves & possibleRay = legal moves.
		return possible;
	}

	/**
     * Generates possible moves for the king when in check.
     *
     * @param from    The position of the king piece.
     * @param isWhite Indicates whether the checking piece is white.
     * @return A bitboard representing possible moves that resolve the check.
     */
	public static long checkKingInCheckMove(int from, boolean isWhite) {
		// Step 1: remove King move into ray of check

		long possibleMoves = ~0L;

		// if king attacked by slider, can't move into the sliders ray unless blocked.
		//		
		long enemyBishops = (!isWhite ? whiteBishopsAM:blackBishopsAM);
		long enemyRooks = (!isWhite ? whiteRooksAM:blackRooksAM);
		long enemyQueens = (!isWhite ? whiteQueensAM:blackQueensAM);

		long horizontal = (0xFFL << ((from / 8) * 8));
		long vertical = (0x0101010101010101L << (from % 8));
		long diagonalPos = generatePositiveDiagonal(from);
		long diagonalNeg = generateNegativeDiagonal(from);
		long kingSquare = 1L << from;




		//SLIDERS
		if ((kingSquare & enemyBishops) != 0) {
			long enemyPiece = (!isWhite ? whiteBishopsBB:blackBishopsBB);
			if ((enemyPiece & diagonalPos) != 0) {
				possibleMoves &= ~diagonalPos;
				possibleMoves |= enemyPiece;
			} else if ((enemyPiece & diagonalNeg) != 0) {
				possibleMoves &= ~diagonalNeg;
				possibleMoves |= enemyPiece;
			}
		}
		if ((kingSquare & enemyRooks) != 0) {
			long enemyPiece = (!isWhite ? whiteRooksBB:blackRooksBB);
			if ((enemyPiece & horizontal) != 0) {
				possibleMoves &= ~horizontal;
				possibleMoves |= enemyPiece;
			} else if ((enemyPiece & vertical) != 0) {	
				possibleMoves &= ~vertical;
				possibleMoves |= enemyPiece;
			}
		}
		if ((kingSquare & enemyQueens) != 0) {
			long enemyPiece = (!isWhite ? whiteQueensBB:blackQueensBB);
			if ((enemyPiece & diagonalPos) != 0) {
				possibleMoves &= ~diagonalPos;
				possibleMoves |= enemyPiece;
			} else if ((enemyPiece & diagonalNeg) != 0) {		
				possibleMoves &= ~diagonalNeg;
				possibleMoves |= enemyPiece;
			} else if ((enemyPiece & horizontal) != 0) {
				possibleMoves &= ~horizontal;
				possibleMoves |= enemyPiece;
			} else if ((enemyPiece & vertical) != 0) {
				possibleMoves &= ~vertical;
				possibleMoves |= enemyPiece;
			}

		}
		return possibleMoves;
		// this bitboard needs to be checked against the current moves.
	}

	/**
     * Checks if the king of the specified color is currently in check.
     *
     * @param isWhite Indicates whether the king being checked is white.
     * @return True if the king is in check; false otherwise.
     */
	public static boolean isInCheck(boolean isWhite) {
		// Step 1: find out if king attacked
		long enemyAttacks = (!isWhite ? whiteAM : blackAM);
		long kingSquare = (isWhite ? whiteKingBB : blackKingBB);

		if ((enemyAttacks & kingSquare) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
     * Generates a list of individual bitboards from a combined bitboard.
     *
     * @param bitboard The combined bitboard representing multiple pieces.
     * @return A list of individual bitboards for each piece.
     */
	public static List<Long> createIndividualBitboards(long bitboard) {
		List<Long> individualBitboards = new ArrayList<>();

		long remainingBits = bitboard;
		while (remainingBits != 0) {
			long leastSignificantBit = remainingBits & -remainingBits;
			individualBitboards.add(leastSignificantBit);
			remainingBits ^= leastSignificantBit;  // Clear the least significant bit
		}

		return individualBitboards;
	}


	/**
     * Updates all bitboards based on the current positions of chess pieces.
     * This method should be called after any changes to the piece positions.
     */
	public static void updateAll() {
		whitePawnsBB = pawnsBB & whiteBB;
		whiteBishopsBB = bishopsBB & whiteBB;
		whiteKnightsBB = knightsBB & whiteBB;;
		whiteRooksBB = rooksBB & whiteBB;;
		whiteQueensBB = queensBB & whiteBB;;
		whiteKingBB = kingsBB & whiteBB;;

		blackPawnsBB = pawnsBB & blackBB;
		blackBishopsBB = bishopsBB & blackBB;
		blackKnightsBB = knightsBB & blackBB;
		blackRooksBB = rooksBB & blackBB;
		blackQueensBB = queensBB & blackBB;
		blackKingBB = kingsBB & blackBB;

		allBB = whiteBB | blackBB;

		whitePawnsAM = Pawn.generateSamePieceAttacks(true);
		whiteBishopsAM = Bishop.generateSamePieceAttacks(true);
		whiteKnightsAM =Knight.generateSamePieceAttacks(true);
		whiteRooksAM = Rook.generateSamePieceAttacks(true);
		whiteQueensAM = Queen.generateSamePieceAttacks(true);
		whiteKingAM = King.generateSamePieceAttacks(true);

		blackPawnsAM = Pawn.generateSamePieceAttacks(false);
		blackBishopsAM = Bishop.generateSamePieceAttacks(false);
		blackKnightsAM =Knight.generateSamePieceAttacks(false);
		blackRooksAM = Rook.generateSamePieceAttacks(false);
		blackQueensAM = Queen.generateSamePieceAttacks(false);
		blackKingAM = King.generateSamePieceAttacks(false);


		whiteAM = whitePawnsAM | whiteBishopsAM | whiteKnightsAM | whiteRooksAM | whiteQueensAM | whiteKingAM;
		blackAM = blackPawnsAM | blackBishopsAM | blackKnightsAM | blackRooksAM | blackQueensAM | blackKingAM;
	}
	
	
	// ### Helper Methods
	/**
     * Generates a bitboard representing the negative diagonal passing through the given square.
     *
     * @param square The index of the square (0 to 63) for which to generate the negative diagonal.
     * @return The bitboard representing the negative diagonal passing through the specified square.
     */
	private static long generateNegativeDiagonal(int square) {
		long diagonal = 0L;
		int row = square / 8;
		int col = square % 8;

		// Generate negative diagonal to the top left
		while (row < 8 && col >= 0) {
			diagonal |= (1L << (row * 8 + col));
			row++;
			col--;
		}
		row = square / 8;
		col = square % 8;
		// Generate negative diagonal to the bottom-right
		while (row >= 0 && col < 8) {
			diagonal |= (1L << (row * 8 + col));
			row--;
			col++;
		}

		return diagonal;
	}

	/**
     * Generates a bitboard representing the positive diagonal passing through the given square.
     *
     * @param square The index of the square (0 to 63) for which to generate the positive diagonal.
     * @return The bitboard representing the positive diagonal passing through the specified square.
     */
	private static long generatePositiveDiagonal(int square) {
		long diagonal = 0L;
		int row = square / 8;
		int col = square % 8;

		// Generate positive diagonal to the top-right
		while (row < 8 && col < 8) {
			diagonal |= (1L << (row * 8 + col));
			row++;
			col++;
		}
		row = square / 8 - 1;
		col = square % 8 - 1;
		// Generate positive diagonal to the bottom-left
		while (row >= 0 && col >= 0) {
			diagonal |= (1L << (row * 8 + col));
			row--;
			col--;
		}

		return diagonal;
	}

	/**
     * Finds the bits between two given bitboards (inclusive) along the same row, column, or diagonal.
     *
     * @param kingBoard   The bitboard of the first position.
     * @param pieceBoard  The bitboard of the second position.
     * @return A bitboard with the bits between the two positions (inclusive) set to 1.
     */
	private static long bitsBetween(long kingBoard, long pieceBoard) {
		// Find the indices of the set bits in each bitboard
		int index1 = Long.numberOfTrailingZeros(kingBoard);
		int index2 = Long.numberOfTrailingZeros(pieceBoard);

		// Determine the minimum and maximum indices

		// Ensure 1 comes before 2
		if (index1 > index2) {
			int temp = index1;
			index1 = index2;
			index2 = temp;
		}

		long mask = (1L << (index2 - index1 + 1)) - 1L;
		mask <<= index1;


		// Apply the mask to get the bits between the two positions
		return mask & ~(kingBoard);
	}
}

