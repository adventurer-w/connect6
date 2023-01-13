package core.player;

import core.game.Move;

import java.util.Scanner;

/* A delegate player of the opponent*/
public class ConsoleDelegate extends Player {
	private final String name;
	public ConsoleDelegate(String name) {
		this.name = name;
	}
	
	@Override
	public Move findMove(Move opponentMove) {
		System.out.print(getColor() + ">");
		Move move = Move.parseMove(_sc.next());
		return move;
	}
	
	private Scanner _sc = new Scanner(System.in);
	
	@Override
	public boolean isManual() {
		return true;
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return name;
	}
}
