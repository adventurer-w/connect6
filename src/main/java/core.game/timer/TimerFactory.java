package core.game.timer;

public class TimerFactory {
	public static GameTimer getTimer(String type, int timeLimit) {
		return new GameTimer(timeLimit);
	}
}
