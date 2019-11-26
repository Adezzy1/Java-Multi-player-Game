package com.elite.game.gameAI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;

import com.elite.framework.misc.RandomNG;
import com.elite.game.assets.Bitmaps;
import com.elite.game.entity.Map;
import com.elite.game.entity.Player;
import com.elite.game.weapons.Weapon;


/**
 * Ai player class
 * 
 * @author - Andreea Merariu
 * @author - Beenita Shah
 *
 *
 */
public class AiPlayer extends Player {

	private float moveTick;
	private final float moveTickMax = 0.08f; // the lower the faster player moves

	public enum Directions {
		UP, DOWN, LEFT, RIGHT;
	};

	public enum TypeAI {
		TOWEAPON, STAND, RANDOM
	};

	private boolean isAI = false;

	// state flags
	public int xOffset, yOffset;
	private boolean dead = false;
	private boolean moving;
	public Directions direction;
	public ArrayList<Directions> directionList = new ArrayList<Directions>();
	public LinkedList<Point> astarPath = new LinkedList<Point>();
	private String id;
	private int playerNo;
	// private String name;
	private ArrayList<AiPlayer> playerList = new ArrayList<AiPlayer>();
	Astar astar;
	public Point start, goal, nextGoal;
	public ArrayList<Integer> xCoord = new ArrayList<Integer>();
	public ArrayList<Integer> yCoord = new ArrayList<Integer>();
	public Map map;
	public ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	public TypeAI aiType;
	public Weapon aiWeapon;

	public AiPlayer(ArrayList<Weapon> weapons, byte x, byte y) {
		isAI = true;
		this.x = x;
		this.y = y;
		this.weapons = weapons;
		System.out.println("aiPlayer created");
		start = new Point();
		goal = new Point();
		nextGoal = new Point();
		aiType = TypeAI.RANDOM;
		
		int i = RandomNG.getRandInt(Bitmaps.player_model_names.size());
		this.setPlayerModel(Bitmaps.player_model_names.get(i));

	}

	public void initalise(Map map) {
		System.out.println("In initalise method.");
		this.map = map;
		astar = new Astar(map);
		astarPath = this.getAstarPath();
		if(astarPath.isEmpty()) {
			reinitialise();
		}
		nextGoal = astarPath.removeFirst();
		directionList = getMovingDirection(astarPath);
	}

	@Override
	public void update(float delta) {
		switch (aiType) {
		case TOWEAPON:
			movementUpdate(delta);
		case STAND:
			break;
		case RANDOM:
			movementUpdate(delta);

		}
	}

	public void movementUpdate(float delta) {
		if (!directionList.isEmpty()) {
			// System.out.println("Direction: Up = "+facingUp + " Down = "+facingDown +"
			// Left = "+facingLeft+ " Right = "+facingRight);
			if (moveTick < 0) {

				if (!listOfKeysPressed.isEmpty()) {
					try {
						currentDirection = listOfKeysPressed.get(0);
						move(MOVE_DELTA, currentDirection);

					} catch (IndexOutOfBoundsException e) {
						// shouldn't ever end up here but might do due to multiple threads
						// having access to 'keysPressed'
						System.err.println("Error: keysPressed is empty when getting direction");
					}
					moveTick = moveTickMax;
					// System.out.println(" X: " + x + " Y: " + y);
				}
			}
			if (!listOfKeysPressed.isEmpty()) {
				try {
					currentDirection = listOfKeysPressed.get(0);
					move(MOVE_DELTA, currentDirection);

				} catch (IndexOutOfBoundsException e) {
					// shouldn't ever end up here but might do due to multiple threads
					// having access to 'keysPressed'
					System.err.println("Error: keysPressed is empty when getting direction");
				}
				// moveTick = moveTickMax;
				// System.out.println(" X: " + x + " Y: " + y);
			}
			if (ifReached()) {
				if (aiWeapon != null) {
					aiWeapon.update(delta);
				}
				switch (directionList.get(0)) {
				case UP:
					setFacingUp();
					removeKeyPress(Player.UP_PRESSED);
					addKeyPress(Player.UP_PRESSED);
					directionList.remove(0);
					break;
				case LEFT:
					setFacingLeft();
					removeKeyPress(Player.LEFT_PRESSED);
					addKeyPress(Player.LEFT_PRESSED);
					directionList.remove(0);
					break;
				case RIGHT:
					setFacingRight();
					removeKeyPress(Player.RIGHT_PRESSED);
					addKeyPress(Player.RIGHT_PRESSED);
					directionList.remove(0);
					break;
				case DOWN:
					setFacingDown();
					removeKeyPress(Player.DOWN_PRESSED);
					addKeyPress(Player.DOWN_PRESSED);
					directionList.remove(0);
					break;
				}
			}
		} else {
			listOfKeysPressed.clear();
			if (aiType == TypeAI.TOWEAPON) {
				pickupWeapon(aiWeapon);
			}
			reinitialise();
		}
	}

	public boolean ifReached() {
		System.out.println("NextGoal: " + nextGoal);
		System.out.println("Current: " + this.x + " " + this.y);
		if (this.x == nextGoal.x && this.y == nextGoal.y && this.xOffset == 0 && this.yOffset == 0) {
			// System.out.println("Move True");
			// System.out.println("Current size of astarPath: "+ astarPath.size());
			nextGoal = astarPath.removeFirst();
			return true;
		} else {
			// System.out.println("Move False");
			return false;
		}
	}

	public ArrayList<AiPlayer> getPlayerList() {
		return playerList;
	}

	public AiPlayer getMyCharacter() {
		for (int i = 0; i < this.getPlayerList().size(); i++) {
			if (this.getPlayerList().get(i).getId().equals(this.getId())) {
				return this.getPlayerList().get(i);
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<Point> getAstarPath() {
		start.setLocation(this.getX(), this.getY());
		goal.setLocation(makeGoal());
		System.out.println("getting path");
		return astar.search(start, goal);
	}

	public ArrayList<Directions> getMovingDirection(LinkedList<Point> path) {
		LinkedList<Point> currentPath = new LinkedList<>();
		for (int i = 0; i < path.size(); i++) {
			currentPath.add(path.get(i));
		}

		Point firstElem = currentPath.removeFirst();
		while (currentPath.size() != 0) {
			Point secondElem = currentPath.removeFirst();
			if (firstElem.getX() - secondElem.getX() > 0)
				directionList.add(Directions.LEFT);
			if (firstElem.getX() - secondElem.getX() < 0)
				directionList.add(Directions.RIGHT);
			if (firstElem.getY() - secondElem.getY() > 0)
				directionList.add(Directions.DOWN);
			if (firstElem.getY() - secondElem.getY() < 0)
				directionList.add(Directions.UP);
			firstElem = secondElem;
			System.out.println("Path size in move: " + astarPath.size());
		}
		return directionList;

	}

	public Point getWeaponLocation() {
		Point toReturn = new Point();
		int weaponGoal = RandomNG.randR(0, weapons.size());
		toReturn.setLocation(weapons.get(weaponGoal).getX(), weapons.get(weaponGoal).getY());
		return toReturn;
	}

	public void reinitialise() {
		int decide = RandomNG.randR(0, 3);
		if (decide == 0) {
			aiType = TypeAI.TOWEAPON;
			this.goal = getWeaponLocation();
			this.start.setLocation(this.x, this.y);
			astarPath = this.getAstarPath();
			nextGoal = astarPath.removeFirst();
			directionList = getMovingDirection(astarPath);
		} else if (decide == 1) {
			aiType = TypeAI.STAND;
		} else if (decide == 2) {
			aiType = TypeAI.RANDOM;
			this.goal.setLocation(makeGoal());
			this.start.setLocation(this.x, this.y);
			astarPath = this.getAstarPath();
			nextGoal = astarPath.removeFirst();
			directionList = getMovingDirection(astarPath);
		}
	}
	
	public Point makeGoal() {
		boolean goalFound = false;
		Point point = new Point();
		while(!goalFound) {
			int goalX = RandomNG.randR(-25, 25);
			int goalY = RandomNG.randR(-25, 25);
			point.setLocation(goalX, goalY);
			goalFound = true;
//			if(isAccessible(point)) {
//				goalFound = true;
//			}
		}
		return point;
	}
	
	public boolean isAccessible(Point point) {
		if(!map.isWall(point.x, point.y)) {
			return false;
		} else {
			if(!map.isWall(point.x+1, point.y) && !map.isWall(point.x-1, point.y) && !map.isWall(point.x, point.y+1) && !map.isWall(point.x, point.y-1)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isMoving() {
		return this.moving;
	}

	// find out if character is dead.
	public boolean isDead() {
		return dead;
	}

	public int getPlayerNumber() {
		return this.playerNo;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNo = playerNumber;
	}
}
