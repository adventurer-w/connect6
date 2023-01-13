/**
 * 
 */
package core.board;

import static core.game.Move.index;
import static core.game.Move.validSquare;


public class Action {
	PieceColor _whoseMove;
	char c;
	char r;
	int k;
	PieceColor[] _board;
	String actionType;
	
	public Action(char c, char r, Board _board,String actionType) {
		this.k=index(c, r);
		this._whoseMove = _board.whoseMove();
		this.actionType = actionType;
		this._board = _board.get_board();
	}
	//luo zi
    protected void set() {
    	assert validSquare(k);
        _board[k] = _whoseMove;
    }
    //ti zi
    protected void pick() {
    	
    }
    //chi zi
    protected void kill() {
    	
    }
}
