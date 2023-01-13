package bf;
import core.board.PieceColor;

public class EvaluationFunction {
	/** ��·������׼�򣬸ý��ͨ���Ŵ��㷨�����Ż��õ� (�����岩�ĵ�������������ʵ��õ���ֵ)*/
	/**�Լ��͵��˵�ƽ����ͬ����Ҫ��������ǿ����*/
//	public final static int[] SCOREOFROAD = {0, 17, 78, 141, 788, 1030, 10000};
//	public final static int[] _SCOREOFROAD = {0, 17, 78, 241, 988, 1030, 10000};
	public final static int[] SCOREOFROAD = {0,9,520,2070,7890,10020,1000000};
	public final static int[] _SCOREOFROAD = {0,3,480,2670,3887,4900,1000000};
	/**
	 *  evaluateChessStatus��������������ֵľ�����Ϣ�����������岩�ĵ������������ı�д��
	 *  @param pieceColor Ҫ�������Ƶ�һ������������ɫ
	 *  @param roadTable ��ǰ�����·��
	 *  @return current ��ǰ������ɫ��Ӧһ����ֵ���������
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
