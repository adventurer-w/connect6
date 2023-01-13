package bf;
import core.board.PieceColor;

public class EvaluationFunction {
	/** 对路的评分准则，该结果通过遗传算法离线优化得到 (六子棋博弈的评估函数论文实验得到的值)*/
	/**自己和敌人的平凡不同，主要是用来加强防御*/
//	public final static int[] SCOREOFROAD = {0, 17, 78, 141, 788, 1030, 10000};
//	public final static int[] _SCOREOFROAD = {0, 17, 78, 241, 988, 1030, 10000};
	public final static int[] SCOREOFROAD = {0,9,520,2070,7890,10020,1000000};
	public final static int[] _SCOREOFROAD = {0,3,480,2670,3887,4900,1000000};
	/**
	 *  evaluateChessStatus方法用来评估棋局的局势信息，根据六子棋博弈的评估函数论文编写。
	 *  @param pieceColor 要评估局势的一方所持棋子颜色
	 *  @param roadTable 当前的棋局路表
	 *  @return current 当前棋子颜色对应一方棋局的评估分数
	 */
	public static int evaluateChessStatus(PieceColor pieceColor,
			RoadTable roadTable) {
		int currentScore = 0;
		int blackRoadScore = 0;
		int whiteRoadScore = 0;
		int [] numberOfBlackRoad = roadTable.black_roads();
		int [] numberOfWhiteRoad = roadTable.white_roads();

		if (pieceColor == PieceColor.BLACK) {
			for (int i = 1; i < 6; i++) {
				blackRoadScore += numberOfBlackRoad[i] * SCOREOFROAD[i];
				whiteRoadScore += numberOfWhiteRoad[i] * _SCOREOFROAD[i];
			}
			currentScore = blackRoadScore - whiteRoadScore;
		}
		if (pieceColor == PieceColor.WHITE) {
			for (int i = 1; i < 6; i++) {
				blackRoadScore += numberOfBlackRoad[i] * _SCOREOFROAD[i];
				whiteRoadScore += numberOfWhiteRoad[i] * SCOREOFROAD[i];
			}
			currentScore = whiteRoadScore - blackRoadScore;
		}
		//System.out.print(currentScore);
		return currentScore;
		
	}
}
