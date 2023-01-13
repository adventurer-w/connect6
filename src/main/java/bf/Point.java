package bf;

import java.util.Comparator;

public class Point implements Comparable<Point>{

	public Point() {
		// TODO Auto-generated constructor stub
	}
	
	public Point(int pos, int score) {
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
	public int compareTo(Point arg0) {
		// TODO Auto-generated method stub
		return this.score - arg0.score;
	}
	
	public static Comparator<Point> scoreComparator = new Comparator<Point>(){
	       public int compare(Point p1, Point p2) {
	           return p2.score - p1.score;
	        }
	};
	
}
