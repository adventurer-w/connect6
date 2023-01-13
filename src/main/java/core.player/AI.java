package core.player;

import core.board.Board;
import core.game.Move;

import java.util.Random;

public abstract class AI extends Player {
	
	public AI() {
	}

	@Override
	public final boolean isManual() {
		// TODO Auto-generated method stub
		return false;
	}

	protected Move firstMove() {
		Random rand = new Random();
		int index = rand.nextInt(firstMoves.length);
		return firstMoves[index];
	}

	public void resume(Move move){
		board.makeMove(move);
	}

	private static Move[] firstMoves = {
			new Move('J', 'K', 'K', 'I'), // 定式1
			new Move('I', 'K', 'L', 'J'), // 定式2
			new Move('I', 'K', 'L', 'K'),
			new Move('I', 'K', 'J', 'L'),
			new Move('J', 'K', 'K', 'J'),
			new Move('I', 'K', 'K', 'L'),
			new Move('J', 'L', 'L', 'J'),
			new Move('J', 'K', 'K', 'K'),
			new Move('J', 'L', 'L', 'L'),
			new Move('K', 'I', 'I', 'K'),
			new Move('J', 'K', 'L', 'J'),
			new Move('K', 'H', 'J', 'K'),
			new Move('L', 'H', 'J', 'L'),
			new Move('J', 'I', 'J', 'K'),
			new Move('I', 'L', 'K', 'L'),
			new Move('J', 'H', 'J', 'L'),
			new Move('J', 'L', 'L', 'K'),
			new Move('J', 'K', 'K', 'L'),
			new Move('L', 'I', 'J', 'K'),
			new Move('I', 'K', 'L', 'K'),
			new Move('J', 'L', 'K', 'L'),
			new Move('L', 'I', 'I', 'K'),
			new Move('I', 'K', 'I', 'L'),
			new Move('L', 'H', 'J', 'K'),
			new Move('J', 'K', 'L', 'K'),
			new Move('J', 'K', 'J', 'L')

	};
}