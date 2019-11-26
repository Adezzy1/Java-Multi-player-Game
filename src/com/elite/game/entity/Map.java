package com.elite.game.entity;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.elite.framework.misc.RandomNG;
import com.elite.game.state.PlayState;
import com.elite.game.weapons.Flamethrower;
import com.elite.game.weapons.Pistol;
import com.elite.game.weapons.Shotgun;
import com.elite.game.weapons.SniperRifle;
import com.elite.game.weapons.Weapon;

/**
 * Map class for representing the game map, all the weapon IDs needed to
 * read locations from a map. As well as the logic that reads a map in from a
 * file.
 * 
 * @author Adeola Aderibigbe
 * @author Fraser Brooks
 */
public class Map implements Serializable{

    private static final long serialVersionUID = 1L;
    
    
    public static final int PISTOL_ID = 200_000;
    public static final int SPAWN_POINT_ID = 100_000;
    public static final int SHOTGUN_ID = 300_000;
    public static final int FLAME_ID = 400_000;   
    public static final int SNIPER_ID = 500_000;
	public int[][] map = new int[256][256];
    private HashMap<Integer, ArrayList<Point>> weaponCoords;
    private ArrayList<Point> spawnPoints;
    
	public Map () {
		
	}
	
	public void initMap(String mapFilePath){
	    weaponCoords = new HashMap<>();
	    spawnPoints = new ArrayList<>();
	    File file = new File("src/assets/maps/" + mapFilePath);
	    readFile(file);
	}
	
	public static String[] getAvailableMaps(){
	    
	    File mapDir = new File("src/assets/maps");
	    
	    File[] files = mapDir.listFiles();
	    
	    ArrayList<String> mapNames = new ArrayList<>();
	    
	    for(File f : files){
	        if(f.isDirectory()){
	            mapNames.add(f.getName());
	            System.out.println(f.getName());
	        }
	    }
	    
	    return mapNames.toArray(new String[1]);
	    
	}
	
	public void readFile(File file) {
		//read the map file
		try {	
			BufferedReader br = new BufferedReader(new FileReader(file));	
			String line;		
			String filecontent = "";
			line = br.readLine();
			for(int i = 0; i < 256; i++){
				filecontent += line += "\n";
				line = br.readLine();
			}
			String [] nlines = filecontent.split("\n");
			for(int y =0; y< 256; y++) {
			    //System.out.println("y:" + y);
				String [] dlines = nlines[y].split(",");
				for(int x =0; x< 256; x++) {
				    //System.out.println("x:" + x);
				    int numberFromFile = Integer.parseInt(dlines[x]);
				    if(numberFromFile >= SNIPER_ID) {
                        numberFromFile -= SNIPER_ID;
                        System.out.println("adding sniper at " + x + "," + y);
                        ArrayList<Point> points = weaponCoords.get(SNIPER_ID);
                        if(points == null){
                            points = new ArrayList<Point>();
                        }
                        points.add(new Point(x - 128,y -128));
                        weaponCoords.put(SNIPER_ID, points);
				    }else if(numberFromFile >= FLAME_ID) {
				    	numberFromFile -= FLAME_ID;
				    	System.out.println("adding flame at " + x + "," + y);
                        ArrayList<Point> points = weaponCoords.get(FLAME_ID);
                        if(points == null){
                            points = new ArrayList<Point>();
                        }
                        points.add(new Point(x - 128,y -128));
                        weaponCoords.put(FLAME_ID, points);
				    }else if(numberFromFile >= SHOTGUN_ID) {
				    	numberFromFile -= SHOTGUN_ID;
				    	System.out.println("adding shotgun at " + x + "," + y);
                        ArrayList<Point> points = weaponCoords.get(SHOTGUN_ID);
                        if(points == null){
                            points = new ArrayList<Point>();
                        }
                        points.add(new Point(x - 128,y -128));
                        weaponCoords.put(SHOTGUN_ID, points);
				    }else if (numberFromFile >= PISTOL_ID) {
                        numberFromFile -= PISTOL_ID;
                        System.out.println("adding pistol at " + x + "," + y);
                        ArrayList<Point> points = weaponCoords.get(PISTOL_ID);
                        if(points == null){
                            points = new ArrayList<Point>();
                        }
                        points.add(new Point(x - 128,y -128));
                        weaponCoords.put(PISTOL_ID, points);
                    }else if (numberFromFile >= SPAWN_POINT_ID) {
                        numberFromFile -= SPAWN_POINT_ID;
                        spawnPoints.add(new Point(x-128, y-128));
                    }
				    
					setTile(x,y, numberFromFile);
				}
			  }
			
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void setTile(int x, int y, int v){ //is v a parameter for different tiles?
        map[x] [y] = v;
    }
    
    public int getTile(int x, int y){
        return map[x+128][y+128];
    }

    public boolean isWall(int x, int y) {
        
        int t = getTile(x,y);
        if(t >= 200 || t <= -200){
            int  remainder = t % 1000;
            int th = (t - remainder) / 1000;
            
            // for transparent tiles, IDs in an 'odd thousand' represent a collidable 'wall' tile. 
            // (i.e 1000<=th<=1999, 3000<=th<=3999, 5000...)
            // 
            // So (th / 2) should have a remainder for wall tiles
            return ((th % 2 != 0));
        }
        
        if(t < 0){
            t *= -1;
        }
        
        return (t >= 100 && t <= 199);
    }
    
    
    public HashMap<Integer, ArrayList<Point>> getWeaponCoords(){
        return weaponCoords;
    }

    public void spawnPlayerRandomly(Player player) {
        System.out.println("spawnPoints = " + spawnPoints.size());
        int spawnIndex = RandomNG.getRandInt(spawnPoints.size());
        Point spawn = spawnPoints.get(spawnIndex);
        
        player.setCoords(spawn.x , spawn.y );
        
    }
    
    public void initWeapons(PlayState playState) {
        HashMap<Integer, ArrayList<Point>> weaponCoords = getWeaponCoords();
        for(Integer i : weaponCoords.keySet()){
            ArrayList<Point> points = weaponCoords.get(i);
            if(points == null){
                continue;
            }
            switch (i) {
            case Map.SNIPER_ID:
                for (Point p : points) {
                    playState.getWeapons().add(new SniperRifle(p.x, p.y));
                }
                break;
            case Map.PISTOL_ID:
                for (Point p : points) {
                    playState.getWeapons().add(new Pistol(p.x, p.y));
                }
                break;
            case Map.FLAME_ID:
                for (Point p : points) {
                    playState.getWeapons().add(new Flamethrower(p.x, p.y));
                }
                break;
            case Map.SHOTGUN_ID:
                for (Point p : points) {
                    playState.getWeapons().add(new Shotgun(p.x, p.y));
                }
                break;
            }
        }
    }
    
    public int[] dropWeapon(Player player){
        
        if(player.getEquipedWeapon() == null){
            return null;
        }
        
        Weapon equipedWeapon = player.getEquipedWeapon();
        
        int i = 2;
        switch (player.currentDirection) {
        case Player.DOWN_PRESSED:
            while (true) {
                if (!isWall(player.getX(), player.getY() - i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() - i);
                    break;
                } else if (!isWall(player.getX() - i, player.getY())) {
                    equipedWeapon.setX(player.getX() - i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX() + i, player.getY())) {
                    equipedWeapon.setX(player.getX() + i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX(), player.getY() + i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() + i);
                    break;
                }
                i++;
            }
            break;
        case Player.UP_PRESSED:
            while (true) {
                if (!isWall(player.getX(), player.getY() + i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() + i);
                    break;
                } else if (!isWall(player.getX() - i, player.getY())) {
                    equipedWeapon.setX(player.getX() - i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX() + i, player.getY())) {
                    equipedWeapon.setX(player.getX() + i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX(), player.getY() - i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() - i);
                    break;
                }
                i++;
            }
            break;
        case Player.LEFT_PRESSED:
            while (true) {
                if (!isWall(player.getX() + i, player.getY())) {
                    equipedWeapon.setX(player.getX() + i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX(), player.getY() - i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() - i);
                    break;
                } else if (!isWall(player.getX(), player.getY() + i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() + i);
                    break;
                } else if (!isWall(player.getX() - i, player.getY())) {
                    equipedWeapon.setX(player.getX() - i);
                    equipedWeapon.setY(player.getY());
                    break;
                }
                i++;
            }
            break;
        case Player.RIGHT_PRESSED:
            while (true) {
                if (!isWall(player.getX() - i, player.getY())) {
                    equipedWeapon.setX(player.getX() - i);
                    equipedWeapon.setY(player.getY());
                    break;
                } else if (!isWall(player.getX(), player.getY() - i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() - i);
                    break;
                } else if (!isWall(player.getX(), player.getY() + i)) {
                    equipedWeapon.setX(player.getX());
                    equipedWeapon.setY(player.getY() + i);
                    break;
                } else if (!isWall(player.getX() + i, player.getY())) {
                    equipedWeapon.setX(player.getX() + i);
                    equipedWeapon.setY(player.getY());
                    break;
                }
                i++;
            }
            break;
        }
        int[] coords = new int[3];
        coords[0] = equipedWeapon.id;
        coords[1] = equipedWeapon.getX();
        coords[2] = equipedWeapon.getY();
        player.setEquipedWeapon(null);
        return coords;
    }
    
    
}
