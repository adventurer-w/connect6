package core.game;

import java.util.Formatter;

public class Move {

    /** Size of a side of the board. */
    public static final int SIDE = 19;
    public static final char MAXCHAR = 'A' + SIDE - 1;

    /** Maximum linearized index, start from 0. */
    static final int MAX_INDEX = SIDE * SIDE - 1;

    /** Constants used to compute linearized indices. */
    private static final int
        STEP_C = 1,
        STEP_R = SIDE,
        INDEX_ORIGIN = -('A' * STEP_C + 'A' * STEP_R);

    public Move(char col0, char row0, char col1, char row1) {
    	
    	set(col0, row0, col1, row1);
    }

    public Move(int index0, int index1)
    {
    	set(index0, index1);
    }
    
    /** Return true iff (C, R) is a valid square designation. */
    public static boolean validSquare(char c, char r) {
        return 'A' <= c && c <= MAXCHAR && 'A' <= r && r <= MAXCHAR;
    }

    /** Return true iff K is a valid linearized index. */
    public static boolean validSquare(int k) {
        return 0 <= k && k <= MAX_INDEX;
    }

    /** Return the linearized index of square C R. */
    public static int index(char c, char r) {
        int k = c * STEP_C + r * STEP_R + INDEX_ORIGIN;
        assert 0 <= k && k <= MAX_INDEX;
        return k;
    }

    /** Return the column letter of linearized index K. */
    public static char col(int k) {
        return (char) (k % STEP_R + 'A');
    }

    /** Return the row digit of linearized index K. */
    public static char row(int k) {
        return (char) (k / STEP_R + 'A');
    }

    /** Returns the source column. */
    public char col0() {
        return _col0;
    }

    /** Returns the source row. */
    public char row0() {
        return _row0;
    }

    /** Returns the destination column. */
    public char col1() {
        return _col1;
    }

    /** Returns the destination row. */
    public char row1() {
        return _row1;
    }

    /** Return the linearized index of my source square. */
    public int index1() {
        return _index0;
    }

    /** Return The linearized index of my destination square. */
    public int index2() {
        return _index1;
    }

    @Override
    public int hashCode() {
        return (_index0 << 8) | _index1;
    }

    @Override
    public boolean equals(Object obj) {
        Move m = (Move) obj;
        return _index0 == m._index0 && _index1 == m._index1;
    }

    /** Return the Move denoted by STR. */
    public static Move parseMove(String str) {
        return new Move(str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3));
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%c%c%c%c", _col0, _row0, _col1, _row1);
        String str = out.toString();
        out.close();
        return str;
    }

    /** Set me to COL0 ROW0 & COL1 ROW1*/
    private void set(char col0, char row0, char col1, char row1) {
        _col0 = col0;
        _row0 = row0;
        _col1 = col1;
        _row1 = row1;
        _index0 = index(col0, row0);
        if (col1 == '@' && row1 == '@')
        	_index1 = -1;
        else
        	_index1 = index(col1, row1);
    }
    
    private void set(int index0, int index1) {
    	_col0 = col(index0);
    	_col1 = col(index1);
    	_row0 = row(index0);
    	_row1 = row(index1);
    	_index0 = index0; 
    	_index1 = index1;
    }
    
    
    public boolean isFirst()
    {
    	return _col1 == '@' && _row1 == '@';
    }

    /** Linearized indices. first square, second square*/
    private int _index0, _index1;

    /** first and second squares. */
    private char _col0, _row0, _col1, _row1;
}
