package com.elite.game.entity;

import java.util.ArrayList;

import com.elite.game.assets.Bitmaps;
import com.elite.game.assets.Sounds;
import com.elite.game.client.NetworkWrapper;
import com.elite.game.engine.Renderer;
import com.elite.game.weapons.Weapon;


/**
 * Player class that handles player movement and status.
 * 
 * @author Fraser Brooks
 * @author Andreea Merariu
 *
 */
public class Player {
    
    public byte x, y;
    public byte xOffset, yOffset;
    public boolean isAlive;
    public boolean facingDown;
    public boolean facingUp;
    public boolean facingLeft;
    public boolean facingRight;

    private boolean isSpy;
    
    private String username;
    private String player_model;
    
    private boolean turnedToShoot;

    public volatile ArrayList<Integer> listOfKeysPressed;
    
    public static final int MAX_HEALTH = 100;
    public static final int UP_PRESSED = 1;
    public static final int RIGHT_PRESSED = 2;
    public static final int DOWN_PRESSED = 3;
    public static final int LEFT_PRESSED = 4;
    public int currentDirection = 1;
    public static int MOVE_DELTA = 4;
    
    private final float moveTickMax = 0.01f; // the lower the faster player moves
    private float moveTick;
    
    private final float directionTickMax = 0.4f;
    private float directionTick;
    
    private Weapon equipedWeapon;
    
    private int health;
    
    public Player(){
        this.x = 0;
        this.y = 0;
        this.xOffset = 0;
        this.yOffset = 0;
        
        this.player_model = "soldier";
        
        this.health = MAX_HEALTH;
        this.isSpy = false;
        
        listOfKeysPressed = new ArrayList<>();

        isAlive = true;
        turnedToShoot = false;
        
        username = "";
        
        moveTick = moveTickMax;
        directionTick = directionTickMax;
        
        
    }
    
    public void addKeyPress(int keyPress){
        if(!listOfKeysPressed.contains(keyPress)){
            listOfKeysPressed.add(0, keyPress);
        }
    }
    
    public void removeKeyPress(int keyPress){
        listOfKeysPressed.remove((Integer) keyPress);
    }
    
    public void updateDirection() {
        if (listOfKeysPressed.isEmpty() || turnedToShoot) {
            return;
        } else {
            switch (listOfKeysPressed.get(0)) {
            case Player.UP_PRESSED:
                setFacingUp();
                break;
            case Player.LEFT_PRESSED:
                setFacingLeft();
                break;
            case Player.RIGHT_PRESSED:
                setFacingRight();
                break;
            case Player.DOWN_PRESSED:
                setFacingDown();
                break;
            }
        }

    }
    
    public void kill(){
        isAlive = false;
    }
    
    public void update(float delta) {
        
        if(getEquipedWeapon() != null){
            getEquipedWeapon().update(delta);
        }
        
        if(!isAlive){
            return;
        }
        
        moveTick -= delta;
        directionTick -= delta;
        
        if(directionTick < 0){
            turnedToShoot = false;
            updateDirection();
            directionTick = directionTickMax;
        }
        
        if (moveTick < 0) {
            
            if (!listOfKeysPressed.isEmpty()) {
                try{
                currentDirection = listOfKeysPressed.get(0);
                move(MOVE_DELTA,currentDirection);
                
                }catch(IndexOutOfBoundsException e){
                    // shouldn't ever end up here but might do due to multiple threads
                    // having access to 'keysPressed'
                    System.err.println("Error: keysPressed is empty when getting direction" );
                }
                moveTick = moveTickMax;
                //System.out.println(" X: " + x + " Y: " + y);
            }
        }

    }
    

    public void bumpBack(){
        move(MOVE_DELTA *-1, currentDirection);
    }
 
    protected void move(int velocity, int direction) {
        switch (direction) {
        case Player.UP_PRESSED:
            addOffsetY((byte) -velocity);
            break;
        case Player.LEFT_PRESSED:
            addOffsetX((byte) -velocity);
            break;
        case Player.RIGHT_PRESSED:
            addOffsetX((byte) velocity);
            break;
        case Player.DOWN_PRESSED:
            addOffsetY((byte) velocity);
            break;
        }
        if (yOffset < 0) {
            yOffset = (byte) (Renderer.TILE_SIZE - MOVE_DELTA);
            addY((byte) -1);
        }
        if (xOffset < 0) {
            xOffset = (byte) (Renderer.TILE_SIZE - MOVE_DELTA);
            addX((byte) -1);
        }
        if (xOffset >= Renderer.TILE_SIZE) {
            xOffset = 0;
            addX((byte) 1);
        }
        if (yOffset >= Renderer.TILE_SIZE) {
            yOffset = 0;
            addY((byte) 1);
        }
    }

   
    public void addX(int v){
        x += v;
    }
    
    public void addY(int v){
        y += v;
    }
    
    public void addOffsetX(int v){
        xOffset += v;
    }
    
    public void addOffsetY(int v){
        yOffset += v;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getXOffset(){
        return xOffset;
    }
    
    public int getYOffset(){
        return yOffset;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public boolean isMoving() {
        return !listOfKeysPressed.isEmpty();
    }
    
    

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight() {
        facingLeft = false;
        facingUp = false;
        facingDown = false;
        this.facingRight = true;
    }

    public boolean isFacingLeft() {
        return facingLeft;
    }

    public void setFacingLeft() {
        facingRight = false;
        facingUp = false;
        facingDown = false;
        this.facingLeft = true;
    }

    public boolean isFacingUp() {
        return facingUp;
    }

    public void setFacingUp() {
        facingLeft = false;
        facingRight = false;
        facingDown = false;
        this.facingUp = true;
    }

    public boolean isFacingDown() {
        return facingDown;
    }

    public void setFacingDown() {
        facingLeft = false;
        facingUp = false;
        facingRight = false;
        this.facingDown = true;
    }

    public void resetDirectionCount(){
        turnedToShoot = true;
        directionTick = directionTickMax;
    }
    

    
    
    
    public void pickupWeapon(Weapon w){
        setEquipedWeapon(w);
    }
    
    public Weapon getWeapon(){
        return getEquipedWeapon();
    }
    

    public boolean getTeam(){
    	return isSpy;
    }
    
    public boolean turnedToShoot(){
        return turnedToShoot;
    }
    
    
    public void setUsername(String name){
        username = name;
    }
    
    public String getUsername(){
        return username;
    }
    
    public boolean setPlayerModel(String model_name){
        if(!Bitmaps.player_model_names.contains(model_name)){
            System.err.println("Error: invalid player model given to " + username);
            return false;
        }
        player_model = model_name;
        return true;
    }
    
    public String getPlayerModel(){
        return player_model;
    }
    
    public void addHealth(int amount){
        if(!isAlive){
            return;
        }
        health += amount;
        if (health <= 0){
            isAlive = false;
            health = 0;
            if(getUsername().equals(NetworkWrapper.username)){
                NetworkWrapper.sendTCP("I am dead".getBytes());
            }
            Sounds.playDeadSound();
        }
        if (health > MAX_HEALTH){
            health = MAX_HEALTH;
        }
    }
    
    public int getHealth(){
        return health;
    }
    
    public void setSpy(boolean b){
        isSpy = b;
    }
    
    
    public boolean isSpy(){
        return isSpy;
    }
    
    public void resetPlayer(){
        health = MAX_HEALTH;
        isAlive = true;
        isSpy = false;
        turnedToShoot = false;
        setEquipedWeapon(null);
    }

    public void setCoords(int x2, int y2) {
        System.out.println(getUsername() + " jumping to x:" + x2 + " y:" + y2);
        this.x = (byte) x2;
        this.y = (byte) y2;
        
    }

    public Weapon getEquipedWeapon() {
        return equipedWeapon;
    }

    public void setEquipedWeapon(Weapon equipedWeapon) {
        this.equipedWeapon = equipedWeapon;
    }
    
    
}
