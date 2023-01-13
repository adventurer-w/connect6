package bf;

import core.board.PieceColor;

import static core.board.PieceColor.BLACK;
import static core.board.PieceColor.WHITE;
import static core.game.Move.SIDE;

public class Road {

	/**
	 * @return the ifleage
	 */
	public boolean isIfleage() {
		return ifleage;
	}
	/**
	 * @param ifleage the ifleage to set
	 */
	public void setIfleage(boolean ifleage) {
		this.ifleage = ifleage;
	}

	//某点四个前进方向的位移量；后退方向  = -前进方向
	//前进方向：下， 右，右下，右上；  {col增量，row增量}
	public static final int _FORWARD[] = { SIDE, 1, SIDE+1, -SIDE+1 };
	
	public Road(int startPos, int dir, int blackNum, int whiteNum, boolean active) {
		super();
		this.startPos = startPos;
		this.dir = dir;
		this.blackNum = blackNum;
		this.whiteNum = whiteNum;
		this.ifleage = active;
	}
	//向该路中添加一个棋子
	public void addStone(PieceColor stone) {
		if (stone == BLACK) blackNum++;
		else if (stone == WHITE) whiteNum++;
	}
	//从该路中移除一个棋子
	public void removeStone(PieceColor stone) {
		if (stone == BLACK) blackNum--;
		else if (stone == WHITE) whiteNum--;
	}
	
	public int getBlackNum() {
		return blackNum;
	}
	public int getWhiteNum() {
		return whiteNum;
	}
	
	public boolean isEmpty() {
		return blackNum == 0 && whiteNum == 0;
	}
	
	public int getStartPos() {
		return startPos;
	}

	public int getDir() {
		return dir;
	}

	private int startPos; 	//该路的起点的编号 （0~360）
	private	int dir;   	  	//该路的方向，起点的前进方向： 下，右，右下，右上

	private int blackNum; 	//该路中黑子的个数
	private int whiteNum; 	//该路中白子的个数
	
	private boolean ifleage; //是否合法的路
		
}
