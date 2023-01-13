package tg04;

import java.util.Comparator;

public class Node implements Comparable<Node>{
    private int pos;
    private int score;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public Node(int pos, int score) {
        super();
        this.pos = pos;
        this.score = score;
    }

    @Override
    public int compareTo(Node o) {
        return this.score - o.score;
    }

    public static Comparator<Node> scoreComparator = new Comparator<Node>() {
        public int compare(Node p1, Node p2) {
            return p2.score - p1.score;
        }
    };

}
