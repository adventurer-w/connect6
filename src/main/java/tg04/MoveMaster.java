package tg04;

import core.game.Move;

import java.util.Comparator;

public class MoveMaster extends Move implements Comparable<MoveMaster>{
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public MoveMaster(int index0, int index1) {
        super(index0, index1);
    }

    public static Comparator<MoveMaster> scoreComparator = new Comparator<MoveMaster>() {
        public int compare(MoveMaster m1, MoveMaster m2) {
            return m2.score - m1.score;
        }
    };
    @Override
    public int compareTo(MoveMaster o) {
        return this.score - o.score;
    }
}
