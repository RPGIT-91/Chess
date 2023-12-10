# Introduction
The following Programn is a Chess Game with move validation
Move generation and validation is done with set wise bitboards for each piece Type and colour.

Only valid chess moves are possible.
In addition to the normal piece moves that includes: en Passant, not moving out of pins and castling, Checks, double Check, Queen Promotion.
Not yet, variable Pawn Promotion, 3-fold repetition.

The pieces are initialized and displayed on the board. In a sense the Board functions like a traditional controller.
When a piece is moved, i.e. "taken" from one square. The individual Pieces are accessed via an interface (PieceI). These pieces implement the move validation logic with the help of the BitBoard class. This class keeps track of the different bitboards. The BitBoards are updated with the moves made on the board.

Given the BitBoard representation and programn handling. The GUI also needs to take the shift into account.

# Methods
from and to values are based on a bitboard -> 0-63 each value representing a different square on the board
## Methods to interact with board
### void movePiece(int from, int to)
Moves the piece from one position to another.

### long showValidMoves(int from)
returns a bitboard of valid moves for a selected square.

## Board Methods
var isWhiteToMove -> true if white piece to move.

### void loadStartPosition() 
reset GameBoard

# Classes
## Board
Controller of Programn, handles interaction between different classes and includes methods for GUI
Keeps track of game state by pushing each move to a stack<>
### GameState
keeps track of enPassant possibility, Castling Rights, move order and captured piece.
## PiecesI
One endpoint for the Board to interact with the different Pieces. Also provides an abstract structure for pieces implementing this Interface.

int getPieceType()
int getPieceColour()
long generateMove(int from, boolean isWhite, GameState previousGameState)
void toggleBB(int from, boolean isWhite)
boolean isWhite()
being the important ones

Additionally includes
static List<Long> createIndividualBitboards(long bitboard)
which is used for creating individual attack masks.
### Pawn
implements Piece Logic
### Knight
implements Piece Logic
### Bishop
implements Piece Logic
### Rook
implements Piece Logic
### Queen
implements Piece Logic
### King
implements Piece Logic

## BitBoards
BitBoards are kept in this class for piece and type, all etc.
static boolean doubleCheck(boolean isWhite) -> method to check for double check
long singleCheck(boolean isWhite) -> method to check for single check. Returns bitboard of moves that don't break a pin

Also keeps and generates attack Masks based on legal piece moves.

### BitBoardHelper
keeps useful methods for bitboard manipulation.
