package game.movegeneration;

import game.movegeneration.pieces.*;

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

	// clear Bitboards on position
	// not used
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

	//doesn't include double check by 2 queens.
	public static boolean doubleCheck(boolean isWhite) {
		int checks = 0;
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);

		long enemyPawns = (!isWhite ? whitePawnsAM:blackPawnsAM);
		long enemyKnights = (!isWhite ? whiteKnightsAM:blackKnightsAM);
		long enemyBishops = (!isWhite ? whiteBishopsAM:blackBishopsAM);
		long enemyRooks = (!isWhite ? whiteRooksAM:blackRooksAM);
		long enemyQueens = (!isWhite ? whiteQueensAM:blackQueensAM);

		if ((kingSquare & enemyPawns) == 1) {
			checks += 1;
		}
		if ((kingSquare & enemyKnights) == 1) {
			checks += 1;
		}
		if ((kingSquare & enemyBishops) == 1) {
			checks += 1;
		}
		if ((kingSquare & enemyRooks) == 1) {
			checks += 1;
		}
		if ((kingSquare & enemyQueens) == 1) {
			checks += 1;
		}

		return (checks < 1);
	}

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
					possibleMoves |= diagonalPos;
				} else if (((!isWhite ? whiteBishopsBB:blackBishopsBB) & diagonalNeg) != 0) {
					possibleMoves |= diagonalNeg;
				}
			}
			if ((kingSquare & enemyRooks) != 0) {
				if (((!isWhite ? whiteRooksBB:blackRooksBB) & horizontal) != 0) {
					possibleMoves |= horizontal;
				} else if (((!isWhite ? whiteRooksBB:blackRooksBB) & vertical) != 0) {
					possibleMoves |= vertical;
				}
			}
			if ((kingSquare & enemyQueens) != 0) {
				if (((!isWhite ? whiteQueensBB:blackQueensBB) & diagonalPos) != 0) {
					possibleMoves |= diagonalPos;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & diagonalNeg) != 0) {
					possibleMoves |= diagonalNeg;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & horizontal) != 0) {
					possibleMoves |= horizontal;
				} else if (((!isWhite ? whiteQueensBB:blackQueensBB) & vertical) != 0) {
					possibleMoves |= vertical;
				}
			}
		}	
		return possibleMoves;
		// this bitboard needs to be checked against the current moves.
	}

	public static long checkPin(int from, boolean isWhite) {	
		// Step 1: piece to move is attacked by enemy bishop, rook, queen. 
		// Step 2: Find out which direction
		// Step 3: check if array is onto king square.
		// Step 4: check if that array is the one from the attacking piece
		// Step 5: check if array is blocked before
		// return bitboard with moves that don't break the pin
		int file = from % 8;
		int rank = from / 8;

		long sliderAM = (!isWhite ? whiteRooksAM | whiteBishopsAM | whiteQueensAM : blackRooksAM | blackBishopsAM | blackQueensAM);
		long sliderSquare = (!isWhite ? whiteQueensBB : blackQueensBB);
		long kingSquare = (isWhite? whiteKingBB : blackKingBB);
		long movingPiece = 1L << from;
		long blocked = allBB & ~kingSquare;
		long possibleRay = ~0L;

		// Step 1
		if ((sliderAM & movingPiece) != 0) {
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
			long inBetween = ((kingSquare - 1) & ~kingSquare) ^ ((movingPiece - 1) & ~movingPiece);
			inBetween &= ~(kingSquare | movingPiece);
			inBetween &= possibleRay;
			
			// If there is a piece between moving piece and king all moves are possible
			if ((inBetween & blocked) != 0) {
				possibleRay = ~0L;
			}
		}
		//Check this long against the current possibleMoves -> possibleMoves & possibleRay = legal moves.
		return possibleRay;
	}

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


	//method to update all BB
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
}

