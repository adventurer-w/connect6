package g04v2;

import java.util.Comparator;

public class Node implements Comparable<Node>{

	public Node() {
		// TODO Auto-generated constructor stub
	}
	
	public Node(int pos, int score) {
		super();
		this.pos = pos;
		this.score = score;
	}

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

	private int pos = 0;
	private int score = 0;
	
	@Override
	public int compareTo(Node arg0) {
		// TODO Auto-generated method stub
		return this.score - arg0.score;
	}
	
	public static Comparator<Node> scoreComparator = new Comparator<Node>(){
	       public int compare(Node p1, Node p2) {
	           return p2.score - p1.score;
	        }
	};
	
}
