package core.game;

import core.board.Board;
import core.board.PieceColor;

import static core.board.PieceColor.EMPTY;


public class Connect6Rule {
    Board _board;
    public static final int[][] FORWARD = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

    public Connect6Rule(Board _board){
        this._board = _board;
    }
    // Is it legal to walk
    public boolean legalMove(Move mov) {
        return Move.validSquare(mov.index1()) && _board.get(mov.index1()) == EMPTY && Move.validSquare(mov.index2())
                && _board.get(mov.index2()) == EMPTY && mov.index1() != mov.index2();
        // Two pieces of the same move fall at different points
    }

    public boolean gameOver() {
        if (_board.getMoveList().isEmpty())
            return false;
        Move lastMove = _board.getMoveList().get(_board.getMoveList().size() - 1);

        return isWin(lastMove.col0(), lastMove.row0()) || isWin(lastMove.col1(), lastMove.row1());
    }

    //Whether the player will win after the player sets the score on the (col,row)
    private boolean isWin(char col, char row) {

        for (int dir = 0; dir < 4; dir++) {
            // The number of consecutive points of the same color from the current point along the dir direction,
            // including the current point
            int len = lengthConnected(col, row, dir, 6);
            // Six already
            if (len == 6) {
                return true;
            } else {
                // Otherwise, move back (6-len) from the current point in the opposite direction of dir direction
                char startCol = (char) (col - FORWARD[dir][0] * (6 - len));
                char startRow = (char) (row - FORWARD[dir][1] * (6 - len));
                // If it is not a legal point, continue to view the next direction
                if (!Move.validSquare(startCol, startRow)) {
                    continue;
                }
                int tempLen = 6 - len;
                len = lengthConnected(startCol, startRow, dir, tempLen);
                // If there are six
                if (len == tempLen) {
                    return true;
                }
            }
        }
        return false;
    }

    // Starting from the point (startcol, startRow),
    // the number of chessmen with the same color as the point continuously at len positions forward along the direction dir
    // The number < = len
    private int lengthConnected(char startCol, char startRow, int dir, int len) {

        int myLen = 0;
        // The color of the chess piece of the point (startcol, startRow)
        PieceColor myColor = _board.whoseMove().opposite();
        for (int j = 0; j < len; j++) {
            char tempCol = (char) (startCol + FORWARD[dir][0] * j);
            char tempRow = (char) (startRow + FORWARD[dir][1] * j);
            if (Move.validSquare(tempCol, tempRow) && _board.get(tempCol, tempRow) == myColor) {
                myLen++;
            } else {
                break;
            }
        }
        return myLen;
    }
}