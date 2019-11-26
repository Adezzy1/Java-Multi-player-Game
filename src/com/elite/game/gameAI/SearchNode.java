package com.elite.game.gameAI;

import java.awt.Point;
import java.util.Comparator;
import com.elite.game.gameAI.Heuristic;

public class SearchNode {
	
	
	private Point location;
	private SearchNode parent;
	private double costSoFar;
	private double distToGo;
	private final boolean isEmpty;

	
	
	public SearchNode(){
		isEmpty = true;
	}

	
	public SearchNode(Point location, SearchNode parent, double costSoFar, Point goal){
		isEmpty = false;
		this.location = location;
		this.parent = parent;
		this.costSoFar = costSoFar;
		this.distToGo = Heuristic.euclidean(location, goal);

		
	}

	//See how far we've come from the start
	
	public double distanceTravelled(){
		return costSoFar;
	}
	
	public void setDistanceTravelled(double cost) {
		costSoFar = cost;
	}

	//An optimistic guess of how far we have to go
	
	public double distanceToGo(){
		return distToGo;
	}

	public void setDistanceToGo(double cost) {
		distToGo = cost;
	}

	public Point getLocation(){
		return location;
	}
	
	public void setParent(SearchNode node) {
		parent = node;
	}

	
	public static Comparator<SearchNode> priorityComparator(){
		return new SearchNodePriorityComparator();
	}

	// return the node that we got to this node from
	 
	public SearchNode getParent(){
		return parent;
	}

	//Empty nodes are what the start nodes' parents are
	public boolean isEmpty(){
		return isEmpty;
	}
	
	public String toString(){
		return ("("+location.getX()+","+location.getY()+")");
	}
	
}

   //This tells us how to sort the frontier (which is a PriorityQueue)

	class SearchNodePriorityComparator implements Comparator<SearchNode>{
	@Override
	public int compare(SearchNode n1, SearchNode n2)
	{
		if ((n1.distanceTravelled() + n1.distanceToGo()) > (n2.distanceTravelled() + n2.distanceToGo())) {
			return 1;
		} else if((n1.distanceTravelled() + n1.distanceToGo()) > (n2.distanceTravelled() + n2.distanceToGo())) {
			return -1;
		} else {
			return 0;
		}
	}
}
