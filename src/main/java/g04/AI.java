package g04;


import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;

import static core.board.PieceColor.BLACK;
import static core.board.PieceColor.EMPTY;
import static core.game.Move.SIDE;
import static g04.Road.DIRECTION;

public class AI extends core.player.AI {
    private BoardMaster board = null;
    PieceColor color;
    private Move myMove;
    private static final int mx_depth = 2;
    ArrayList<MoveMaster> record = new ArrayList<>();
    private static final int mx = Integer.MAX_VALUE;
    //���Ŀ��
    public final static int WIDTH = 40;

    ArrayList<Integer> nodeList = new ArrayList<>();
    public boolean[] visable = new boolean[SIDE * SIDE];
    ArrayList<Node> nodesList = new ArrayList<>();
    public int XS = 0;


    public AI() {
    }

    @Override
    public String name() {
        return "G04-QunXin";
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new BoardMaster();
    }

    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {//����
            Move move = firstMove();
            board.makeMove(move);
            XS=1;
            return move;
        } else{
            board.makeMove(opponentMove);
        }
//        System.out.println(XS);
        if (canWin() != null) { //win!!
            myMove = canWin();
            board.makeMove(myMove);
            return myMove;
        }

        myMove=null;
        color = board.whoseMove();

        if(calculateThreats(color)==0){ //�Է�û��в
            //DTSS��в����������
            for (int i = 3; i <= 25; i += 2) {
                if(record.size()>=1) record.clear();
                if (DTSS(i)) {
                    board.makeMove(myMove);
                    return myMove;
                }
            }
        }

        if(record.size()>=1) record.clear();
        if(calculateThreats(color)==2){
            ArrayList<MoveMaster> moves = twoThreatsDefense();
            moves.sort(MoveMaster.scoreComparator);
            myMove=moves.get(0);
        }else{
            alphaBetaSearch(-mx, mx, mx_depth);
        }

        if(myMove == null){
            ArrayList<MoveMaster> m = freeAttack();
            m.sort(MoveMaster.scoreComparator);
            myMove = m.get(0);
        }

        board.makeMove(myMove);

        return myMove;
    }


    int calculateThreats(PieceColor color) {
        RoadList fourList,fiveList;
        ArrayList <Integer> poslist = new  ArrayList<>();
        boolean visit[] = new boolean[SIDE*SIDE];
        for(int i = 0; i < SIDE*SIDE; i++) visit[i] =false;

        //�ҵ��Է���ɫ�����е���·����·
        if (color == BLACK) {
            fourList = board.roadTable.getWbRoads()[0][4];fiveList = board.roadTable.getWbRoads()[0][5];
        } else {
            fourList = board.roadTable.getWbRoads()[4][0];fiveList = board.roadTable.getWbRoads()[5][0];
        }

        //��������·����·������в
        if (fiveList.size() + fourList.size() == 0) return 0;

        //ʼ��ͷ���
        RoadList roadList = (fourList.size() == 0 ? fiveList : fourList);

        int start = roadList.get(0).getPos();
        int dir = roadList.get(0).getDir();

        for (int i = 0; i < 6; i++){
            //�õ�����·�ϵ��λ�ã����ж��Ƿ���������
            int pos = start + DIRECTION[dir] * i;
            if(board.get(pos) != EMPTY) continue;

            //������һ�ţ��ټ�����в��
            board.addRoads(pos,color);
            int t = fourList.size() + fiveList.size();
            board.removeRoads(pos,color);
            //�����вΪ0�������ǵ���в

            if(t==0) return 1;
        }

        //�����ٿ�����˫���Ƕ�
        //�ҳ������������µ�λ�ã�ȥ��
        for (int i = 0; i < fiveList.size(); i++){
            start = fiveList.get(i).getPos();
            dir = fiveList.get(i).getDir();
            for (int j = 0; j < 6; j++){
                int pos = start + DIRECTION[dir] *j;
                if(board.get(pos) != EMPTY) continue;

                if (!visit[pos]){
                    visit[pos] = true;
                    poslist.add(pos);
                }
            }
        }

        for (int i = 0; i < fourList.size(); i++){
            start = fourList.get(i).getPos();
            dir = fourList.get(i).getDir();
            for (int j = 0; j < 6; j++){
                int pos = start + DIRECTION[dir] * j;
                if(board.get(pos) != EMPTY) continue;

                if (!visit[pos]){
                    visit[pos] = true;
                    poslist.add(pos);
                }
            }
        }

        boolean is2 = false;
        int temp = poslist.size();
        for (int i = 0; i < temp && !is2; i++){
            for (int j = i + 1; j < temp && !is2; j++){
                board.addRoads(poslist.get(i), color);
                board.addRoads(poslist.get(j), color);
                //��������������ӣ���������·����·���ϣ�����˫��в
                if (fourList.size() + fiveList.size() == 0){
                    is2 = true;
                }
                board.removeRoads(poslist.get(i), color);
                board.removeRoads(poslist.get(j), color);
            }
        }

        return (is2 ? 2 : 3);
    }

    //˫��в����
    ArrayList<MoveMaster> twoThreatsAttack() {
        RoadList twoList,threeList;
        ArrayList<MoveMaster> ansList = new ArrayList<>();
        nodeList.clear();

        for(int i = 0; i <SIDE*SIDE;i++) visable[i] = false;
        //���ܵĵ� ���ڵ�·
        if(board.whoseMove() == BLACK){
            twoList = board.roadTable.getWbRoads()[2][0];
            threeList = board.roadTable.getWbRoads()[3][0];
        }else{
            twoList = board.roadTable.getWbRoads()[0][2];
            threeList = board.roadTable.getWbRoads()[0][3];
        }

        //Ǳ����
        for(Road road : twoList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()] *i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        for(Road road: threeList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()] *i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        int sz = nodeList.size();

        //�õ���в��
        for (int i = 0; i < sz; i++) {
            for (int j = i + 1; j < sz; j++) {
                //����������Ǳ����
                board.addRoads(nodeList.get(i),board.whoseMove());
                board.addRoads(nodeList.get(j),board.whoseMove());
                //�����ʱ��в>=2,˵������
                if (calculateThreats(board.whoseMove().opposite()) >= 2) {
                    MoveMaster tm = new MoveMaster(nodeList.get(i), nodeList.get(j));
                    ansList.add(tm);
                }
                board.removeRoads(nodeList.get(i), board.whoseMove());
                board.removeRoads(nodeList.get(j), board.whoseMove());
            }
        }

        return ansList;
    }

    //�Ƿ��б�ʤ�߷�
    public MoveMaster canWin(){
        MoveMaster move = null;

        Road winRoad = board.roadTable.findColorWin(board.whoseMove());
        if(winRoad != null){
            System.out.println("win!");
            int t,step1 =-1, step2 = -1;
            int start = winRoad.getPos();
            int dir = winRoad.getDir();

            for(int i = 0; i < 6; i++){
                t = start + i* DIRECTION[dir];
                if(board.get(t) == EMPTY)
                    if(step1 < 0) step1 = t;
                    else if(step2<0) step2 = t;
            }

            //˵��ֻ��Ҫ��һ�ţ���һ�������
            if(step2 < 0){
                for(int i = 0; i < SIDE*SIDE ;i++)
                    if(i!= step1&&board.get(i) == EMPTY){
                        step2 = i;
                        break;
                    }
            }
            move = new MoveMaster(step1, step2);
        }
        return move;
    }

    //û����в��ѡ��õ���
    public  ArrayList<MoveMaster> freeAttack() {
        ArrayList<MoveMaster> moves = new ArrayList<>();
        nodesList.clear();
        findblanks();
        nodesList.sort(Node.scoreComparator);

        //�����������
        int choose_num = Math.min(WIDTH, nodesList.size() / 2);
        int index = 0;

        for(Node node:  nodesList){
            for(int i = ++index; i < choose_num; i++){
                MoveMaster move = new MoveMaster(node.getPos(),  nodesList.get(i).getPos());

                board.addRoads(move.index1(), board.whoseMove());
                board.addRoads(move.index2(), board.whoseMove());

                move.setScore(RoadTable.valueEstimate( board.whoseMove(),  board.roadTable,XS));

                board.removeRoads(move.index1(),  board.whoseMove());
                board.removeRoads(move.index2(),  board.whoseMove());

                moves.add(move);
            }
            choose_num--;
        }
        return moves;
    }

    void addOne(RoadList la, RoadList lb){
        for(int j = 0;  j < la.size(); j++){
            Road road = la.get(j);
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + i * DIRECTION[road.getDir()];
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    board.addRoads(pos,board.whoseMove());
                    //���Ƴ���
                    if (la.size() + lb.size() == 0){
                        nodeList.add(pos);
                    }
                    board.removeRoads(pos,board.whoseMove());
                    visable[pos] = true;
                }
            }
        }
    }

    //һ����в
    ArrayList<MoveMaster> oneThreatsDefense(){
        RoadList fourList,fiveList;
        ArrayList<MoveMaster> ansList = new ArrayList<>();
        nodeList.clear();
        int average,sum = 0,num=0;
        for(int i = 0; i <SIDE*SIDE;i++) visable[i] = false;

        //�Է�����в
        if(board.whoseMove() == BLACK){
            fourList = board.roadTable.getWbRoads()[0][4];
            fiveList = board.roadTable.getWbRoads()[0][5];
        }else{
            fourList = board.roadTable.getWbRoads()[4][0];
            fiveList = board.roadTable.getWbRoads()[5][0];
        }

        for(int j = 0;  j < fourList.size(); j++){
            Road road = fourList.get(j);
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + i * DIRECTION[road.getDir()];
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    board.addRoads(pos,board.whoseMove());
                    //���Ƴ���
                    if (fourList.size() + fiveList.size() == 0){
                        nodeList.add(pos);
                    }
                    board.removeRoads(pos,board.whoseMove());
                    visable[pos] = true;
                }
            }
        }

        for(int j = 0;  j < fiveList.size(); j++){
            Road road = fiveList.get(j);
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + i * DIRECTION[road.getDir()];
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    board.addRoads(pos,board.whoseMove());
                    //���Ƴ���
                    if (fiveList.size() + fourList.size() == 0){
                        nodeList.add(pos);
                    }
                    board.removeRoads(pos,board.whoseMove());
                    visable[pos] = true;
                }
            }
        }

        //�ڶ�������
        nodesList.clear();
        findblanks();
        nodesList.sort(Node.scoreComparator);

        //����ƽ��ֵ��
        for(Node node:nodesList) sum = node.getScore() + sum;
        average = sum/nodesList.size();
        for(int i=nodesList.size()-1;i>=0;i--){
            if(nodesList.get(i).getScore() >= average) break;
            nodesList.remove(nodesList.size()-1);
        }


        for(Integer node:nodeList){
            for(Node nodes: nodesList){
                if(node != nodes.getPos()){
                    MoveMaster move = new MoveMaster(node, nodes.getPos());

                    board.addRoads(move.index1(),board.whoseMove());
                    board.addRoads(move.index2(),board.whoseMove());

                    move.setScore(RoadTable.valueEstimate(board.whoseMove(), board.roadTable,XS));

                    board.removeRoads(move.index1(),board.whoseMove());
                    board.removeRoads(move.index2(),board.whoseMove());

                    ansList.add(move);
                }
            }
        }

        return ansList;
    }
    void findblanks(){
        for(int i = 0; i < SIDE*SIDE; i++){
            if(board.myBord[i] > 0 && board.get(i)== EMPTY){
                board.addRoads(i,board.whoseMove());
                int score = RoadTable.valueEstimate(board.whoseMove(), board.roadTable,XS);
                board.removeRoads(i, board.whoseMove());
                nodesList.add(new Node(i, score));
            }
        }
    }

//    void findblanks(Road road) {
//        for (int i = 0; i < 6; i++) {
//            int pos = road.getPos() + DIRECTION[road.getDir()] *i;
//            if (board.get(pos) != EMPTY)
//                continue;
//            if (visable[pos] == false) {
//                nodeList.add(pos);
//                visable[pos] = true;
//            }
//        }
//    }



    //������в
    ArrayList<MoveMaster> twoThreatsDefense(){
        RoadList fourList,fiveList;
        for(int i = 0; i <SIDE*SIDE;i++) visable[i] = false;
        ArrayList<MoveMaster> ansList = new ArrayList<>();
        nodeList.clear();

        //�Է�����в
        if(board.whoseMove() == BLACK){
            fourList = board.roadTable.getWbRoads()[0][4];
            fiveList = board.roadTable.getWbRoads()[0][5];
        }else{
            fourList = board.roadTable.getWbRoads()[4][0];
            fiveList = board.roadTable.getWbRoads()[5][0];
        }

        //����в�Ŀհ׿鴢��
        for(Road road : fourList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()]*i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        for(Road road : fiveList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()]*i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        //�����Ƴ���в�ĸ����߷�
        for (int i = 0; i < nodeList.size(); i++)
        {
            for (int j = i + 1; j < nodeList.size(); j++)
            {
                board.addRoads(nodeList.get(i),board.whoseMove());
                board.addRoads(nodeList.get(j),board.whoseMove());

                if (fourList.size() + fiveList.size() == 0){
                    MoveMaster move = new MoveMaster(nodeList.get(i), nodeList.get(j));
                    move.setScore(RoadTable.valueEstimate(board.whoseMove(), board.roadTable,XS));
                    ansList.add(move);
                }

                board.removeRoads(nodeList.get(i),board.whoseMove());
                board.removeRoads(nodeList.get(j),board.whoseMove());
            }
        }


        return ansList;
    }


//    void addTwoStep(RoadList list){
//        for(Road road : list){
//            for (int i = 0; i < 6; i++) {
//                int pos = road.getPos() + DIRECTION[road.getDir()]*i;
//                if (board.get(pos) != EMPTY)
//                    continue;
//                if (visable[pos] == false) {
//                    nodeList.add(pos);
//                    visable[pos] = true;
//                }
//            }
//        }
//    }
    ArrayList<MoveMaster> manyThreatsDefense(){
        RoadList fourList,fiveList;
        for(int i = 0; i <SIDE*SIDE;i++) visable[i] = false;
        ArrayList<MoveMaster> ansList = new ArrayList<>();
        nodeList.clear();

        //�Է�����в
        if(board.whoseMove() == BLACK){
            fourList = board.roadTable.getWbRoads()[0][4];
            fiveList = board.roadTable.getWbRoads()[0][5];
        }else{
            fourList = board.roadTable.getWbRoads()[4][0];
            fiveList = board.roadTable.getWbRoads()[5][0];
        }
        for(int i = 0; i <SIDE*SIDE;i++) visable[i] = false;
        //����в�Ŀհ׿鴢��
        for(Road road : fourList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()]*i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        for(Road road : fiveList){
            for (int i = 0; i < 6; i++) {
                int pos = road.getPos() + DIRECTION[road.getDir()]*i;
                if (board.get(pos) != EMPTY)
                    continue;
                if (visable[pos] == false) {
                    nodeList.add(pos);
                    visable[pos] = true;
                }
            }
        }
        int minT = fourList.size() + fiveList.size();


        for (int i = 0; i < nodeList.size(); i++)
        {
            for (int j = i + 1; j < nodeList.size(); j++)
            {
                board.addRoads(nodeList.get(i),board.whoseMove());
                board.addRoads(nodeList.get(j),board.whoseMove());
                int nowT = fourList.size() + fiveList.size();
                if (nowT < minT){
                    ansList.clear();
                    minT = nowT;
                    ansList.add(new MoveMaster(nodeList.get(i),nodeList.get(j)));
                }else if(nowT == minT) {
                    ansList.add(new MoveMaster(nodeList.get(i),nodeList.get(j)));
                }
                board.removeRoads(nodeList.get(i),board.whoseMove());
                board.removeRoads(nodeList.get(j),board.whoseMove());
            }
        }
        return ansList;
    }


    boolean DTSS(int depth) {
        //�����˻�û�ҵ������п�
        if (depth == 0)
            return false;

        // �öԷ���
        if (color != board.whoseMove()) {
            // �²�ס������Ӯ
            if (calculateThreats(board.whoseMove()) >= 3) {
                myMove = record.get(0);
                return true;
            }
            // �Է����з������������з���
            ArrayList<MoveMaster> movesList = twoThreatsDefense();
            for (MoveMaster move : movesList) {
                board.makeMove(move);
                record.add(move);
                boolean flag = DTSS(depth - 1);
                record.remove(record.size() - 1);
                board.undoMove(move);

                if (!flag)  return false;
            }
            return true;
        }
        else { //�ֵ����ǿ�
            //����Է�����в���ҷ�û��
            if (calculateThreats(color) > 0 && calculateThreats(color.opposite()) == 0)
                return false;

            // ���п��е�˫��в����
            ArrayList<MoveMaster> movesList = twoThreatsAttack();
            for (MoveMaster move : movesList) {
                board.makeMove(move);
                record.add(move);
                boolean flag = DTSS(depth - 1);
                record.remove(record.size() - 1);
                board.undoMove(move);
                if (flag) return true;
            }
            return false;
        }

    }

    public int alphaBetaSearch(int alpha, int beta, int depth) {
        int value, best = -Integer.MAX_VALUE;

        if (board.gameOver() || depth <= 0) {
            return RoadTable.valueEstimate(color, board.getRoadTable(),XS);
        }

        ArrayList<MoveMaster> moves = null;
        int threats = calculateThreats(board.whoseMove());

        if (threats == 0) {
            moves = freeAttack();
        } else if (threats == 1) {
            moves = oneThreatsDefense();
        } else if (threats == 2) {
            moves = twoThreatsDefense();
        } else {
            moves = manyThreatsDefense();
        }

        // ����ʽ����
        moves.sort(MoveMaster.scoreComparator);

        for (MoveMaster move : moves) {
            board.makeMove(move);
            value = -alphaBetaSearch(-beta, -alpha, depth - 1);
            board.undoMove(move);

            if (value > best) {
                best = value;
                if (best > alpha) {
                    alpha = best;
                }
                if (value >= beta) {
                    break;
                }
            }
            if (depth == mx_depth && value >= best) {
                board.makeMove(move);
                color = color.opposite();
                record.clear();
                if (!DTSS(5)) {
                    myMove = move;
                }
                color = color.opposite();
                board.undoMove(move);
            }
        }
        return alpha;
    }


}
