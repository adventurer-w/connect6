package bf;

import core.board.Board;
import core.board.PieceColor;
import core.game.Move;

import java.util.ArrayList;

import static core.board.PieceColor.*;
import static core.game.Move.SIDE;
import static g04v2.Road._FORWARD;


public class Board4AI extends Board {
	/** 周围16子*/
	int arround[] = {1,2,-1,-2, SIDE,SIDE*2,-SIDE, -SIDE*2, 
			SIDE+1, 2*SIDE + 2, -SIDE-1, -(2*SIDE + 2),-SIDE+1, 2-2*SIDE,SIDE-1 ,2*SIDE - 2};
	
	/** 下棋的范围*/
	int battle[] = new int[SIDE * SIDE];	
	
	/** 走法生成时获取棋点的宽度 */
	public final static int POINTSWIDTH=36;
	
	/** 棋盘的路表信息 */
	private RoadTable roadTable = new RoadTable();
	
	/** 返回路表 */
	public RoadTable getRoadTable() {
		return roadTable;
	}
	
	/**构造*/
	public Board4AI() {
		super();	
		roadTable.clear();
		for(int i = 0; i < SIDE*SIDE; i++) battle[i] = 0;
		updateBattleForMove(180);
		
	}
	
	/**更新战场范围*/
	public void updateBattleForMove (Move mov) {
		int index0 = mov.index1();
		updateBattleForMove(index0);
		int index1 = mov.index2();
		updateBattleForMove(index1);
	}
	public void updateBattleForUndo (Move mov) {
		int index0 = mov.index1();
		updateBattleForUndo(index0);
		int index1 = mov.index2();
		updateBattleForUndo(index1);
	}
	public void updateBattleForMove (int pos) {
		battle[pos]++;
		for(int i =0 ; i < 16; i++){
			int index = pos + arround[i];
			if(Move.validSquare(index))
			battle[index]++;
		}
	}
	public void updateBattleForUndo (int pos) {
		battle[pos]--;
		for(int i =0 ; i < 16; i++){
			int index = pos + arround[i];
			if(Move.validSquare(index))
			battle[index]--;
		}
	}
	/**真正落子*/
	@Override
	public void makeMove(Move mov) {
		changeRoads(mov);
		super.makeMove(mov);
		updateBattleForMove(mov);
	}
	public void undoMove(Move mov) {		
		super.undo();
		unchangeRoads(mov);
		updateBattleForUndo(mov);
	}
	/**只对路表修改*/
	private void changeRoads(Move move) {

		int index0 = move.index1();
		changeRoads(index0);
		int index1 = move.index2();
		changeRoads(index1);
	}
	private void changeRoads(int pos, PieceColor color) {
		ArrayList<Road> affectedRoads = roadTable.getAffectedRoads(pos);
		if (affectedRoads.isEmpty())
			return;
		for (Road road : affectedRoads) {
			// 将受到影响的路，从原先的路表中删除
			roadTable.removeRoad(road);
			// 从该路中加入本棋手所下颜色的棋子
			road.addStone(color);
			// 将新的路，添加到相应的路表中
			roadTable.addRoad(road);
		}
	}
	private void changeRoads(int pos) {
		changeRoads(pos,whoseMove());
	}
	private void unchangeRoads(Move move) {
		int index0 = move.index1();
		unchangeRoads(index0);
		int index1 = move.index2();
		unchangeRoads(index1);
	}
	// 撒销对路表信息的修改	
	private void unchangeRoads(int pos,PieceColor piece) {
		ArrayList<Road> affectedRoads = roadTable.getAffectedRoads(pos);
		if (affectedRoads.isEmpty())
			return;
		for (Road road : affectedRoads) {
			// 将受到影响的路，从原先的路表中删除
			roadTable.removeRoad(road);
			
			// 从该路中删除本棋手所下颜色的棋子
			road.removeStone(piece);
			// 将新的路，添加到相应的路表中
			roadTable.addRoad(road);
		}
	}
	private void unchangeRoads(int pos) {
		unchangeRoads(pos,whoseMove());
	}

	/**计算迫着， color受到的威胁数（堵住所有四子五子至少要下几棵棋)*/
	int countAllThreats(PieceColor color) {
		//
		ArrayList <Integer> poslist = new  ArrayList<>();
		boolean visit[] = new boolean[SIDE*SIDE];
		for(int i = 0; i < SIDE*SIDE; i++) visit[i] =false;
		
		//找到所有的有效的四路和五路
		RoadList four = (color == WHITE ? roadTable.getPlayerRoads()[4][0] : roadTable.getPlayerRoads()[0][4]);
		RoadList five = (color == WHITE ? roadTable.getPlayerRoads()[5][0] : roadTable.getPlayerRoads()[0][5]);
		
		//不存在四路和五路 也就不存在必须要赌的棋子
		if (five.size() + four.size() == 0) return 0;
		
		//是否是单迫着 ，就看是否有一个子 把所有威胁接触
		RoadList roadList = (four.size() == 0 ? five : four);

		Road tp =roadList.iterator().next();
		int pos_start = tp.getStartPos();
		int dir = tp.getDir();
		for (int i = 0; i < 6; i++){
			int pos = pos_start + _FORWARD[dir] * i;
			if(get(pos) != EMPTY) continue;
			changeRoads(pos,color);
			int t = four.size() + five.size();
			unchangeRoads(pos,color);

			if(t== 0) return 1;
		}
		//判断是双迫着还是多迫着，思路：找出所以可能用来堵的位置，遍历 看看存不存在 直接解决所有威胁的情况
		//visit控制防止重复添加点， 因为可能一个点的棋子堵住好几个威胁
		for (Road rd:five){
			 pos_start = rd.getStartPos();
			 dir = rd.getDir();
			for (int j = 0; j < 6; j++){
				int pos = pos_start + _FORWARD[dir] *j;
				if(get(pos) != EMPTY) continue;
				
				if (!visit[pos]){
					visit[pos] = true;
					poslist.add(pos);
				}
			}
		}

		for (Road rd:four){
			 pos_start = rd.getStartPos();
			 dir = rd.getDir();
			for (int j = 0; j < 6; j++){
				int pos = pos_start + _FORWARD[dir] * j;
				if(get(pos) != EMPTY) continue;
				
				if (!visit[pos]){
					visit[pos] = true;
					poslist.add(pos);
				}
			}
		}
		
		boolean flag = false;
		int temp = poslist.size();

		for (int i = 0; i < temp && !flag; i++){
			for (int j = i + 1; j < temp && !flag; j++){
				changeRoads(poslist.get(i), color);
				changeRoads(poslist.get(j), color);
				//下俩子 把所有四路 五路堵上
				if (four.size() + five.size() == 0){
					flag = true;
				}
				unchangeRoads(poslist.get(i), color);
				unchangeRoads(poslist.get(j), color);
			}
		}
		return (flag ? 2 : 3);
    }
	
	/**以下为着法生成 */
	
	
	/**以下三个数据结构用于着法生成*/
	ArrayList<Integer> pointlist =  new ArrayList<>();
	private boolean[] vis = new boolean [SIDE*SIDE];
	ArrayList<Point> pointslist =  new ArrayList<>();
	/**两个寻找空位的辅助函数*/
	/**寻找战场范围内的空白点*/
	void findblanks(){
		for(int i = 0; i < SIDE*SIDE; i++){
			if(battle[i] > 0 && get(i)== EMPTY){
				changeRoads(i);
				int score = EvaluationFunction.evaluateChessStatus(whoseMove(), roadTable);
				unchangeRoads(i);
				pointslist.add(new Point(i, score));
			}				
		}
	}
	/**寻找路上的空白点*/
	void findblanks(Road road) {
		for (int i = 0; i < 6; i++) {
			int pos = road.getStartPos() + _FORWARD[road.getDir()] *i;
			if (get(pos) != EMPTY)
				continue;
			if (vis[pos] == false) {
				pointlist.add(pos);
				vis[pos] = true;
			}
		}
	}
	
	/** 一 ， 生成必胜着法（我方已经四连或者五连)*/
	public Move4AI findwinMoves(){
		Move4AI move = null;
		Road winroad = roadTable.findWinMove(whoseMove());
		if(winroad != null){
			int index,index0 =-1, index1 = -1;
			int startpos = winroad.getStartPos();
			int dir = winroad.getDir();
		    for(int i = 0; i < 6; i++){
		    	index = startpos + i* _FORWARD[dir];
		    	if(get(index) == EMPTY)
		    		if(index0 < 0) index0 = index;
		    		else if(index1<0) index1 = index;
		    }
		    if(index1 < 0){
		    	for(int i = 0; i < SIDE*SIDE ;i++){
		    		if(get(i) == EMPTY && i!= index0){
		    			index1 = i;
		    			break;
		    		}
		    	}
		    }
		    move = new Move4AI(index0, index1);
		 }
		return move;
	}
	
	/** 二 ， 一般情况下的着法生成 寻找好点*/
	public  ArrayList<Move4AI> findGenerateMoves() {
		ArrayList<Move4AI> moves = new ArrayList<>();
		pointslist.clear();
		findblanks();
		pointslist.sort(Point.scoreComparator);
		//筛选点，减少搜索时间
		int choose_num = POINTSWIDTH < pointslist.size()/2 ? POINTSWIDTH:pointslist.size()/2;
		int index = 0;
		for(Point point: pointslist){
			for(int i = ++index; i < choose_num; i++){
				Move4AI move = new Move4AI(point.getPos(), pointslist.get(i).getPos());
				changeRoads(move);
				move.setScore(EvaluationFunction.evaluateChessStatus(whoseMove(), roadTable));
				unchangeRoads(move);
				moves.add(move);
			}
			choose_num--;
		}
		return moves;
	}

	

	ArrayList<Move4AI> findDoubleThreats() {
		ArrayList<Move4AI> movelist = new ArrayList<>();
		pointlist.clear();

		for(int i = 0; i <SIDE*SIDE;i++) vis[i] = false;
		RoadList myTwo = (whoseMove() == BLACK ? roadTable.getPlayerRoads()[2][0] : roadTable.getPlayerRoads()[0][2]);
		RoadList myThree = (whoseMove() == BLACK ? roadTable.getPlayerRoads()[3][0] : roadTable.getPlayerRoads()[0][3]);
		for(Road road : myTwo){
			findblanks(road);
		}
		for(Road road: myThree){
			findblanks(road);
		}
		int len = pointlist.size();

		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				//从潜力点中进行筛选，得到真正威胁点
				changeRoads(pointlist.get(i));
				changeRoads(pointlist.get(j));
				if (countAllThreats(whoseMove().opposite()) >= 2) {
					Move4AI testmove = new Move4AI(pointlist.get(i), pointlist.get(j));
					movelist.add(testmove);
				}
				unchangeRoads(pointlist.get(i));
				unchangeRoads(pointlist.get(j));
			}
		}
		
		return movelist;
	}
	ArrayList<Move4AI> findSingleBlocks(){
		ArrayList<Road> four,five;
		RoadList R1,R2;
		ArrayList<Move4AI> movelist = new ArrayList<>();
		pointlist.clear();
		for(int i = 0; i <SIDE*SIDE;i++) vis[i] = false;
		//对方连四或者连五

		if(whoseMove() == BLACK){
			four = new ArrayList<Road> (roadTable.getPlayerRoads()[0][4]);
			five = new ArrayList<Road> (roadTable.getPlayerRoads()[0][5]);
			R1 = roadTable.getPlayerRoads()[0][4];
			R2 = roadTable.getPlayerRoads()[0][5];
		}else{
			four = new ArrayList<Road> (roadTable.getPlayerRoads()[4][0]);
			five = new ArrayList<Road> (roadTable.getPlayerRoads()[5][0]);
			R1 = roadTable.getPlayerRoads()[4][0];
			R2 = roadTable.getPlayerRoads()[5][0];
		}

		int lenfour = four.size(), lenfive = five.size();
        for(int j = 0;  j < lenfour; j++){
        	Road road = four.get(j);
        	for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + i * _FORWARD[road.getDir()];
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					changeRoads(pos);
					//能破除，
					if (R1.size() + R2.size() == 0){
						pointlist.add(pos);
					}				
					unchangeRoads(pos);		
					vis[pos] = true;
				}
			}
        }
		for(int j =0 ; j < lenfive ; j++){
			Road road = five.get(j);
			for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + i *_FORWARD[road.getDir()];
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					changeRoads(pos);
					//能破除，
					if (R1.size() + R2.size() == 0){
						pointlist.add(pos);
					}				
					unchangeRoads(pos);		
					vis[pos] = true;
				}
			}
		}
		pointslist.clear();
		findblanks();
		pointslist.sort(Point.scoreComparator);
		
		int sum = 0;
		for(Point point:pointslist){
			sum = point.getScore() + sum;
		}
		int average = sum/pointslist.size();
		int num = 0;
		for(int i = 0; i < pointslist.size();i++){
			if(pointslist.get(i).getScore() < average -1)
				num = i;
		}
		while(pointslist.size() > num){
			pointslist.remove(pointslist.size()-1);
		}
		
		for(Integer point:pointlist){
			for(Point points: pointslist){
				if(point != points.getPos()){
					Move4AI move = new Move4AI(point, points.getPos());
					changeRoads(move);
					move.setScore(EvaluationFunction.evaluateChessStatus(whoseMove(), roadTable));
					unchangeRoads(move);
					movelist.add(move);
				}
			}	
		}
		return movelist;
	}
	//两个子全部用来堵
	ArrayList<Move4AI> findDoubleBlocks(){
		ArrayList<Move4AI> movelist = new ArrayList<>();
		pointlist.clear();
		for(int i = 0; i <SIDE*SIDE;i++) vis[i] = false;
		//对方连四或者连五
		RoadList four = (whoseMove() == WHITE ? roadTable.getPlayerRoads()[4][0] : roadTable.getPlayerRoads()[0][4]);
		RoadList five = (whoseMove() == WHITE ? roadTable.getPlayerRoads()[5][0] : roadTable.getPlayerRoads()[0][5]);
		//先把空白块存起来
		for(Road road : four){
			for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + _FORWARD[road.getDir()]*i;
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					pointlist.add(pos);
					vis[pos] = true;
				}
			}
		}
		for(Road road: five){
			for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + _FORWARD[road.getDir()]*i;
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					pointlist.add(pos);
					vis[pos] = true;
				}
			}
		}
		
		//计算空白块，看看它能不能真正破除威胁
		for (int i = 0; i < pointlist.size(); i++)
		{
			for (int j = i + 1; j < pointlist.size(); j++)
			{
				changeRoads(pointlist.get(i));
				changeRoads(pointlist.get(j));
				//能破除
				if (four.size() + five.size() == 0){
					Move4AI move = new Move4AI(pointlist.get(i), pointlist.get(j));
					move.setScore(EvaluationFunction.evaluateChessStatus(whoseMove(), roadTable));
					movelist.add(move);
				}										
				unchangeRoads(pointlist.get(i));
				unchangeRoads(pointlist.get(j));
			}
		}
		//if(movelist.isEmpty()) System.out.println("hh");
		return movelist;
	}
	ArrayList<Move4AI> findTripleBlocks(){
		ArrayList<Move4AI> movelist = new ArrayList<>();
		pointlist.clear();
		for(int i = 0; i <SIDE*SIDE;i++) vis[i] = false;
		//对方连四或者连五
		RoadList four = (whoseMove() == WHITE ? roadTable.getPlayerRoads()[4][0] : roadTable.getPlayerRoads()[0][4]);
		RoadList five = (whoseMove() == WHITE ? roadTable.getPlayerRoads()[5][0] : roadTable.getPlayerRoads()[0][5]);
		int lenfour = four.size();
		int lenfive = five.size();
		for(int i = 0; i <SIDE*SIDE;i++) vis[i] = false;
		//先把空白块存起来
		for(Road road : four){
			for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + i *_FORWARD[road.getDir()];
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					pointlist.add(pos);
					vis[pos] = true;
				}
			}
		}
		for(Road road: five){
			for (int i = 0; i < 6; i++) {
				int pos = road.getStartPos() + _FORWARD[road.getDir()] *i ;
				if (get(pos) != EMPTY)
					continue;
				if (vis[pos] == false) {
					pointlist.add(pos);
					vis[pos] = true;
				}
			}
		}
		int minthreats = four.size() + five.size();
		
		//if(pointlist.size() <= 1) System.out.println("bhb <1");
		for (int i = 0; i < pointlist.size(); i++)
		{
			for (int j = i + 1; j < pointlist.size(); j++)
			{
				changeRoads(pointlist.get(i));
				changeRoads(pointlist.get(j));
				//能破除，
				int nowthreats = four.size() + five.size();
				if (nowthreats < minthreats){
					movelist.clear();
					minthreats = nowthreats;
					movelist.add(new Move4AI(pointlist.get(i),pointlist.get(j)));
				}
				else if(nowthreats == minthreats) {
					movelist.add(new Move4AI(pointlist.get(i),pointlist.get(j)));
				}						
				unchangeRoads(pointlist.get(i));
				unchangeRoads(pointlist.get(j));
			}
		}
		return movelist;
	}
	//获取可能成为威胁的点
//	void findPotentialPoints( ){
//		//我方连二或者连三的点即为潜力点
//		//用估值函数？ no 连二 连三 才可可能 连四练武
//		RoadList myTwo = (whoseMove() == BLACK ? roadTable.getPlayerRoads()[2][0] : roadTable.getPlayerRoads()[0][2]);
//		RoadList myThree = (whoseMove() == BLACK ? roadTable.getPlayerRoads()[3][0] : roadTable.getPlayerRoads()[0][3]);
//		for(Road road : myTwo){
//			findblanks(road);
//		}
//		for(Road road: myThree){
//			findblanks(road);
//		}
//		int lenOwnTwo = myTwo.size();
//		int lenOwnThree = myThree.size();
//
//
//		for (int k = 0; k < lenOwnThree; k++){
//			findblanks(myThree.get(k));
//		}
//
//		for (int k = 0; k < lenOwnTwo; k++){
//			findblanks(myTwo.get(k));
//		}
//	}
	
}
