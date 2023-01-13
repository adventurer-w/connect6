package core.game;

public class GameResult {
    private final String black;
    private final String white;
    private final String winner;
    private final int steps;
    private final String endReason;

    public GameResult(String black, String white, String winner, int steps, String endReason) {
        this.black = black;
        this.white = white;
        this.winner = winner;
        this.steps = steps;
        this.endReason = endReason;

    }

    /**
     * Score of player name in this match
     * Win: 2 points; Average: 1 point; Negative: 0 point
     * @param name
     * @return
     */
    public int score(String name) {
        if ("NONE".equals(this.winner))
            return 1;
        if (name.equals(this.winner)) {
            return 2;

        }
        return 0;
    }

    /**
     * Get the opponent of player name in this match
     * @param name
     * @return Name's opponent's name
     */
    public String getOpponent(String name){
        if (this.black.equals(name)){
            return this.white;
        }
        return this.black;
    }

    public String toString() {
        return "\tBlack:" + this.black + "\n\tWhite:" + this.white + "\n\tWinner:" + this.winner + "\n\tSteps:" + this.steps + "\n\tEndReason:" + this.endReason + "\n";

    }
}
