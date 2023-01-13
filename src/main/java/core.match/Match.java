package core.match;

import core.game.Game;
import core.player.Player;

import java.util.ArrayList;

public class Match {

    private final int gameNumbers;

    public Match(int gameNumbers, Player one, Player another) {
        this.gameNumbers = gameNumbers;
        this.one = one;
        this.another = another;
    }
    private final Player one;

    private final Player another;

    public ArrayList<Game> getGames() {
        Player black = this.one;
        Player white = this.another;
        ArrayList<Game> games = new ArrayList<>(this.gameNumbers);

        for (int i = 0; i < this.gameNumbers; i++) {
            try {
                Player bClone = (Player)black.clone();
                Player wClone = (Player)white.clone();
                games.add(new Game(bClone, wClone));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            Player temp = black;
            black = white;
            white = temp;
        }
        return games;
    }
}