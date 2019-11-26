package com.elite.game.gameAI;

import java.awt.Point;

public class Vector implements Comparable<Vector> {
	private double dx, dy;
	private Point centre;

	//Make a vector from two points i. the source and destination
	public Vector(Point source, Point dest){
		this.dx = dest.x - source.x;
		this.dy = dest.y - source.y;
		// normalise
		double distance =Math.sqrt(Math.pow(dx, 2) + Math.pow(dy,2));
	}

	public double getX(){
		return dx;
	}

	public double getY(){
		return dy;
	}

	@Override
	public int compareTo(Vector arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
