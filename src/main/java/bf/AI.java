package bf;

import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.ArrayList;

public class AI extends core.player.AI {
	// ���������
	private static final int MAX_DEPTH = 3;
	/* ��¼һ����������� */
	ArrayList<Move4AI> moveOrder = new ArrayList<>();

	public AI() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Move findMove(Move opponentMove) {
		if (opponentMove == null) {
			Move move = firstMove();
			board.makeMove(move);
			return move;
		} else {
			board.makeMove(opponentMove);
		}

		bestMove = board.findwinMoves();
		if (bestMove != null) {
			board.makeMove(bestMove);
			return bestMove;
		}

		// ��¼�ҷ���ɫ
		color = board.whoseMove();
		// ��Ϊ��findwinmoves��ԭ�� ���Դ����ص������㿪ʼ
		moveOrder.clear();
		for (int i = 3; i <= 27; i += 2) {
			if (DTSS(i)) {
				board.makeMove(bestMove);

				return bestMove;
			}
		}

		alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE,1, MAX_DEPTH);

		if (bestMove == null) {
			ArrayList<Move4AI> moves = board.findGenerateMoves();
			moves.sort(Move4AI.scoreComparator);
			bestMove = moves.get(0);
		}
		board.makeMove(bestMove);
		return bestMove;
	}

	boolean DTSS(int depth) {
		// depthΪ0�������ﵽ�����Ȼ�û���ҵ�����˫���ŵ������return false��
		if (depth == 0)
			return false;
		// ���ҷ�����ʱ
		if (color == board.whoseMove()) {
			// ����Է������ҷ�������в�������ҷ����ڶԷ�û����в
			if (board.countAllThreats(color) > 0 && board.countAllThreats(color.opposite()) == 0)
				return false;

			// �ҵ��ҷ������Ϊ˫���ŵ������ŷ�
			ArrayList<Move4AI> movesList = board.findDoubleThreats();
			for (Move4AI move : movesList) {
				board.makeMove(move);
				moveOrder.add(move);
				boolean flag = DTSS(depth - 1);
				moveOrder.remove(moveOrder.size() - 1);
				board.undoMove(move);
				// �����㷨�����ڼ��ɷ���true
				if (flag)
					return true;
			}
			return false;
		}
		// �Է�����ʱ
		else {
			// ����²�ס�ҷ����ڶԷ�����в
			if (board.countAllThreats(board.whoseMove()) >= 3) {
				bestMove = moveOrder.get(0);
				return true;
			}
			// �ҵ��Է������µ������ŷ�
			ArrayList<Move4AI> movesList = board.findDoubleBlocks();
			for (Move4AI move : movesList) {
				board.makeMove(move);
				moveOrder.add(move);
				boolean flag = DTSS(depth - 1);
				moveOrder.remove(moveOrder.size() - 1);
				board.undoMove(move);
				// �����㷨������ȫ�����Գ���˫���ţ����� ����ʧ��
				if (!flag)
					return false;
			}
			return true;
		}

	}

	public int alphaBeta(int alpha,int beta, int nw, int depth) {
		if (board.gameOver() || depth <= 0) {
			int evaluateScore = EvaluationFunction.evaluateChessStatus(color, board.getRoadTable());
			return evaluateScore;
		}
		ArrayList<Move4AI> moves = null;
		int threats = board.countAllThreats(board.whoseMove());
		if (threats == 0) {
			moves = board.findGenerateMoves();
		} else if (threats == 1) {
			moves = board.findSingleBlocks();
		} else if (threats == 2) {
			moves = board.findDoubleBlocks();
		} else {
			moves = board.findTripleBlocks();
		}

		if(nw==1){
			// ����ʽ����
			int tAlpha;
			moves.sort(Move4AI.scoreComparator);
			for (Move4AI move : moves) {

				board.makeMove(move);
				tAlpha = alphaBeta(alpha, beta,0, depth - 1);
				board.undoMove(move);

				if(tAlpha > alpha){
					alpha = tAlpha;
					if (depth == MAX_DEPTH){
						board.makeMove(move);
						color = color.opposite();
						moveOrder.clear();
						// ��һ������DTSS���� ��ֹ�Լ�����ʧ��
						if (!DTSS(7)) {
							bestMove = move;
						}

						color = color.opposite();
						board.undoMove(move);
					}
				}
				if(alpha >= beta){
					return beta;
				}
			}
			return alpha;
		}else{
			//min
			// ����ʽ����
			int tBeta;
			moves.sort(Move4AI.scoreComparator);
			for (Move4AI move : moves) {
				board.makeMove(move);
				tBeta = alphaBeta(alpha, beta,1, depth - 1);
				board.undoMove(move);
				if(beta > tBeta){
					beta = tBeta;
				}
				if(alpha >= beta){
					return alpha;
				}
			}
			return beta;
		}

	}


	private Move bestMove;

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "g06-t";
	}

	@Override
	public void playGame(Game game) {
		super.playGame(game);
		board = new Board4AI();
	}

	// �Լ����е�����
	private Board4AI board = null;
	PieceColor color;
}
