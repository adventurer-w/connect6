package core.player;

import core.board.Board;
import core.board.PieceColor;
import core.game.Game;
import core.game.GameResult;
import core.game.Move;
import core.game.timer.GameTimer;

import java.util.ArrayList;

/**
 * A generic Connect6 Player.
 * 
 * @author
 */
public abstract class Player implements Comparable<Player>, Cloneable {

	public abstract boolean isManual();

	public abstract String name();

	public Board board = new Board();

	private ArrayList<GameResult> gameResults = new ArrayList<>();

	// 添加当前棋手参与的棋局的结果
	public void addGameResult(GameResult result) {
		gameResults.add(result);
	}

	public ArrayList<GameResult> gameResults() {
		return gameResults;
	}

	// 计算当前棋手所有棋局的总得分：获胜得2分，平局得1分，落败得0分
	public int scores() {
		int scores = 0;
		for (GameResult result : gameResults) {
			scores += result.score(this.name());
		}
		return scores;
	}

	/**
	 * 当前棋手与其对手opponent得对弈结果统计
	 * @param opponent 当前棋手的对手
	 * @return
	 */
	public int[] getGameStatistics(String opponent){
		int[] statistics = new int[3];
		for (GameResult result : gameResults) {
			if (result.getOpponent(this.name()).equals(opponent)) {
				statistics[result.score(this.name())]++;
			}
		}
		return statistics;
	}

	/**
	 * 显示当前棋手与其对手opponent的对弈结果统计
	 * @param opponent
	 */
	public void showGameStatistics(String opponent){
		//获得当前棋手与opponent的对弈结果统计
		int[] statistics = getGameStatistics(opponent);
		//向控制台输出对弈结果
		System.out.println("Game Statistics (" + this.name() + " vs " + opponent +")");
		System.out.println("\twin: " + statistics[2] + ", " + "lose: " + statistics[0] + ", " +
				"draw: " + statistics[1]);
	}

	public PieceColor getColor() {
		return _myColor;
	}

	/** A Player that will play MYCOLOR in GAME. */
	public void setColor(PieceColor myColor) {
		_myColor = myColor;
	}

	/** Return the game I am playing in. */
	public Game game() {
		return _game;
	}

	/** Join a game. */
	public void playGame(Game game) {
		_game = game;
		timer.addObserver(_game);
	}

	/**
	 * Return a legal move for me according to my opponent's move, and at that
	 * moment, I am facing a board after the opponent's move. Abstract method to be
	 * implemented by subclasses.
	 */
	public abstract Move findMove(Move opponentMove) throws Exception;

	/** The game I am playing in. */
	private Game _game;

	/** The color of my pieces. */
	private PieceColor _myColor;
	private GameTimer timer;
	@Override
	public int compareTo(Player arg0) {
		// TODO Auto-generated method stub
		return arg0.scores() - this.scores();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
    }

	public void setTimer(GameTimer timer) {
		// TODO Auto-generated method stub
		this.timer = timer;
	}
	
	public void stopTimer() {
		timer.stop();
	}
	public void startTimer() {
		timer.start();
	}
}
