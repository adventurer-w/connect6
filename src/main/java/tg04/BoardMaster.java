package tg04;

import core.board.Board;
import core.board.PieceColor;
import core.game.Move;

import java.util.ArrayList;

import static core.game.Move.SIDE;


public class BoardMaster extends Board {
    
    @Override
    public void makeMove(Move mov) {
        addRoads(mov.index1(),whoseMove());
        addRoads(mov.index2(),whoseMove());
        super.makeMove(mov);
        updateBoard(mov.index1());
        updateBoard(mov.index2());
    }
    public void undoMove(Move mov) {
        super.undo();
        removeRoads(mov.index1(), whoseMove());
        removeRoads(mov.index2(), whoseMove());
        updateUndo(mov.index1());
        updateUndo(mov.index2());
    }


    public RoadTable getRoadTable() {
        return roadTable;
    }
    public BoardMaster() {
        super();
        roadTable.initTable();
        for(int i = 0; i < SIDE*SIDE; i++) myBord[i] = 0;
        updateBoard(180);

    }

    public void addRoads(int pos, PieceColor color) {
        ArrayList<Road> affectedRoads = roadTable.getAffectedRoads(pos);
        if (affectedRoads.isEmpty())
            return;
        for (Road road : affectedRoads) {
            roadTable.removeRoad(road);
            road.addStone(color);
            roadTable.addRoad(road);
        }
    }

    public void removeRoads(int pos,PieceColor piece) {
        ArrayList<Road> affectedRoads = roadTable.getAffectedRoads(pos);
        if (affectedRoads.isEmpty())
            return;
        for (Road road : affectedRoads) {
            roadTable.removeRoad(road);
            road.removeStone(piece);
            roadTable.addRoad(road);
        }
    }

    public void updateBoard (int pos) {
        myBord[pos]++;
        for(int i =0 ; i < 16; i++){
            int index = pos + nearBy[i];
            if(Move.validSquare(index))
                myBord[index]++;
        }
    }
    public void updateUndo (int pos) {
        myBord[pos]--;
        for(int i =0 ; i < 16; i++){
            int index = pos + nearBy[i];
            if(Move.validSquare(index))
                myBord[index]--;
        }
    }

    //周围16位置
    int nearBy[] = {1, 2, -1, -2, SIDE, SIDE * 2, -SIDE, -SIDE * 2, SIDE + 1, 2 * SIDE + 2, -SIDE - 1, -(2 * SIDE + 2), -SIDE + 1, 2 - 2 * SIDE, SIDE - 1, 2 * SIDE - 2};
    //下棋的范围
    int myBord[] = new int[SIDE * SIDE];
    //路表
    public RoadTable roadTable = new RoadTable();
}
