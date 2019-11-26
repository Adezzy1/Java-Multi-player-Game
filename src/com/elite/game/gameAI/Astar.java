package com.elite.game.gameAI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;

import com.elite.game.entity.Map;
import com.elite.game.state.PlayState;

/**
 * A* pathfinding algorithm
 * 
 * @author - Andreea Merariu
 * @author - Beenita Shah
 *
 *
 */
public class Astar{

	private PriorityQueue<SearchNode> frontier;
	private LinkedList<SearchNode> visited;
	private Point goal = new Point();
	private SearchNode current;
	private double costSoFar;
	public Map map = new Map();

	public Astar(Map map) {
		this.map = map;
	}

	//Returns a list of waypoints between the start and goal points
	//frontier = open list, visited = closed list. costSoFar is cheapest gx.
		public LinkedList<Point> search(Point start, Point goal){
		//System.out.println("Start: "+start);
		//System.out.println("Goal: "+goal);
		// reset all the fields
		this.frontier = new PriorityQueue<SearchNode>(SearchNode.priorityComparator()); // sorts by cost + heuristic.
		this.visited = new LinkedList<SearchNode>();
		this.goal = goal;
		this.costSoFar = 0;
		this.current = new SearchNode(start, null, costSoFar, goal);

		// first check if we're already there somehow.
		if (start.equals(goal)){
			System.out.println("1");
			return new LinkedList<Point>();
		}

		// generate frontier by getting 8 surrounding tiles
		//addNeighbours();
		LinkedList<Point> list = new LinkedList<Point>();
		frontier.add(current);
		boolean success = false;
		// explore best looking (first) option in frontier until there are no more unexplored reachable states
		while (!frontier.isEmpty() && !success){
		//for(int i = 0; i < 2000; i++) {
			//System.out.println("while loop");
			// 	set current to the head of the frontier
			current = frontier.poll();
			visited.add(current);
			
			if ((current.getLocation().x == goal.x) && (current.getLocation().y == goal.y)){
				//System.out.println("Goal Found");
				//System.out.println("Goal Parent: "+current.getParent().getLocation());
				list = reconstructPath();
				success = true;
				//return list;
				break;
			}
			
			ArrayList<SearchNode> allneighbours = addNeighbours();
			for(SearchNode node : allneighbours) {
				if(inVisited(node) == true) {
					//System.out.println("Visited Check works");
					continue;
				}
				
				if(inFrontier(node) == false) {
					frontier.offer(node);
				}
				
				double newcost = current.distanceTravelled() + 1;
				if(newcost >= node.distanceTravelled()) {
					continue;
				}
				
				node.setDistanceTravelled(newcost);
				node.setParent(current);
				costSoFar = newcost;
			}
			
			//System.out.println("End of for");
			//list = reconstructPath();
			/*System.out.println("Frontier List: ");
			for(SearchNode node: frontier) {
				System.out.println(node.getLocation().x + " " + node.getLocation().y);
			}*/
			//System.out.println("VistedLength "+visited.size());
			
		}
		//System.out.println("search");
		System.out.println("List Size: " +list.size());
		return list;
	}

		
		private ArrayList<SearchNode> addNeighbours() {
			//System.out.println("addNeighbours");
			ArrayList<SearchNode> neighbours = new ArrayList<SearchNode>();
			int cost = 10;
			int wallCost = 10000;
			
			if(current.getLocation().x+1 != 128) {
				if(map.isWall(current.getLocation().x+1, current.getLocation().y) == false) {
					//System.out.println("Changed x worked");
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x+1, current.getLocation().y);
					child = new SearchNode(nextPoint, current, costSoFar + cost, this.goal);
					neighbours.add(child);
				} else{
					//System.out.println("Changed x worked");
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x+1, current.getLocation().y);
					child = new SearchNode(nextPoint, current, costSoFar + wallCost, this.goal);
					neighbours.add(child);
				}
			}
			
			if(current.getLocation().x-1 != -128) {
				if(map.isWall(current.getLocation().x-1, current.getLocation().y) == false) {
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x-1, current.getLocation().y);
					child = new SearchNode(nextPoint, current, costSoFar + cost, this.goal);
					//System.out.println("Changed x worked");
					neighbours.add(child);
				} else{
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x-1, current.getLocation().y);
					child = new SearchNode(nextPoint, current, costSoFar + wallCost, this.goal);
					//System.out.println("Changed x worked");
					neighbours.add(child);
				}
			}
			
			if(current.getLocation().y+1 != 128) {
				if(map.isWall(current.getLocation().x, current.getLocation().y+1) == false) {
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x, current.getLocation().y+1);
					child = new SearchNode(nextPoint, current, costSoFar + cost, this.goal);
					neighbours.add(child);
					//System.out.println("Changed y worked");
				} else{
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x, current.getLocation().y+1);
					child = new SearchNode(nextPoint, current, costSoFar + wallCost, this.goal);
					neighbours.add(child);
					//System.out.println("Changed y worked");
				}
			}
			
			if(current.getLocation().y-1 != -128) {
				if(map.isWall(current.getLocation().x, current.getLocation().y-1) == false) {
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x, current.getLocation().y-1);
					child = new SearchNode(nextPoint, current, costSoFar + cost, this.goal);
					neighbours.add(child);
					//System.out.println("Changed y worked");
				} else{
					Point nextPoint = new Point();
					SearchNode child = new SearchNode();
					nextPoint.setLocation(current.getLocation().x, current.getLocation().y-1);
					child = new SearchNode(nextPoint, current, costSoFar + wallCost, this.goal);
					neighbours.add(child);
					//System.out.println("Changed y worked");
				}
			}
			
			//System.out.println("Length of Neighbours: "+neighbours.size());
			return neighbours;
			
		}
		
		private boolean inFrontier(SearchNode check) {
			for(SearchNode node : frontier) {
				if((node.getLocation().x == check.getLocation().x) && (node.getLocation().y == check.getLocation().y)) {
					return true;
				}
			}
			return false;
		}
		
		private boolean inVisited(SearchNode check) {
			for(int i = 0; i < visited.size(); i++) {
				if((visited.get(i).getLocation().x == check.getLocation().x) && (visited.get(i).getLocation().y == check.getLocation().y)) {
					return true;
				}
			}
			return false;
		}

	//When we have found the goal as a search node, follow the parents back to the source
	 // Builds the list from the front so we don't need to reverse it
	 //return a list of points that lead from the start to the goal
	private LinkedList<Point> reconstructPath(){
		SearchNode node = current;
		LinkedList<Point> list = new LinkedList<Point>();
		//list.add(node.getLocation());
		while (node != null)
		{
			list.add(node.getLocation());
			node = node.getParent();
		}
		
		Collections.reverse(list);
		return list;
	}



}
