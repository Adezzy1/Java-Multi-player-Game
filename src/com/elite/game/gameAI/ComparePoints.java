package com.elite.game.gameAI;

import java.awt.Point;
import java.util.Comparator;

public class ComparePoints implements Comparator<Point>{
	@Override
	public int compare(Point p1, Point p2){
		if (p1.equals(p2))
			return 0;
		else if (p1.x < p2.x)
			return -1;
		else if (p1.x > p2.x)
			return 1;
		else if (p1.y < p2.y)
			return -1;
		else
			return 1;
	}

}
