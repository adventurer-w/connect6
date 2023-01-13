package core.game.ui;

import core.game.timer.GameTimer;

import java.util.Observer;

public interface GameUI extends Observer{
	//在UI上放置可视化的计时器
	public void setTimer(GameTimer bTimer, GameTimer wTimer);
}
