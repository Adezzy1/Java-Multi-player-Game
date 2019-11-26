package com.elite.game.gameAI;

import java.awt.Point;

public class Heuristic {

	
		// the Euclidean distance between two points, a and b
		public static double euclidean(Point a, Point b){
			
			double dist;
			try{
				dist = Math.sqrt(Math.pow(Math.abs(a.getX()) - Math.abs(b.getX()), 2) + Math.pow(Math.abs(a.getY()) - Math.abs(b.getY()), 2));
			}catch (NullPointerException e){
				return Double.MAX_VALUE;
			}
			
			return dist;
		}
		
	
}
