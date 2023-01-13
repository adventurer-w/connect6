package tg04;

import core.board.PieceColor;

import static core.board.PieceColor.BLACK;
import static core.board.PieceColor.WHITE;
import static core.game.Move.SIDE;

public class Road {
    private int pos;    //路的起点号 范围0-360
    private int dir;  //该路的方向，起点的前进方向：下，右，右下，右上
    private int blackNum;    //黑子数
    private int whiteNum;    //白子数
    private boolean legal; //该路是否合法 1合法 0不合法
    public static final int[] DIRECTION = {SIDE, 1, SIDE + 1, -SIDE + 1};

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public void setBlackNum(int blackNum) {
        this.blackNum = blackNum;
    }

    public int getBlackNum() {
        return blackNum;
    }

    public void setWhiteNum(int whiteNum) {
        this.whiteNum = whiteNum;
    }

    public int getWhiteNum() {
        return whiteNum;
    }

    public boolean getLegal() {
        return legal;
    }

    public void setLegal(boolean legal) {
        this.legal = legal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Road) {
            Road road = (Road) obj;
            if ((road.pos == pos) && (road.dir == dir)
                    && (road.blackNum == blackNum)
                    && (road.whiteNum == whiteNum)) {
                return true;
            }
            return false;
        }
        return false;
    }


    public Road(int pos, int dir, int blackNum, int whiteNum, boolean legal) {
        super();
        this.pos = pos;
        this.dir = dir;
        this.blackNum = blackNum;
        this.whiteNum = whiteNum;
        this.legal = legal;
    }

    //添加棋子
    public void addStone(PieceColor stone) {
        if (stone == BLACK) blackNum++;
        else if (stone == WHITE) whiteNum++;
//        if(blackNum + whiteNum >= 7) {
//            System.out.println("h");
//        }
    }

    //移除棋子
    public void removeStone(PieceColor stone) {
        if (stone == BLACK) blackNum--;
        else if (stone == WHITE) whiteNum--;
    }

    public boolean isEmpty() {
        return blackNum == 0 && whiteNum == 0;
    }

}
