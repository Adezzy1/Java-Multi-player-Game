package com.elite.game.entity;

/**
 * This class represents a Player on the client side that is not the client. 
 * i.e one of the other players in the game.
 * 
 * <p> In the main loop of <b>PlayState</b> UDP updates are received and
 * these contain updates for all <b>OtherPlayer</b>'s in the form of the
 * coordinates that the real player this OtherPlayer represents is at. 
 * This OtherPlayer then mimics a real player by adding the appropriate 
 * keys to the Player superclass that moves accordingly in it's update method.
 * </p>
 * <p>This allows this <b>OtherPlayer</b> to keep up with the exact location of 
 * the real player that it represents. Of course there is some slight latency but even
 * with the serve being hosted west coast US this is not an issue.
 * </p>
 * 
 * <p> Note that if it is detected that this representation of the actual player
 * has lagged too far behind it will 'jump' to the correct coordinates.</p>
 * 
 * @author Fraser Brooks
 *
 */
public class OtherPlayer extends Player{
    
    private int XfromServer;
    private int YfromServer;
    
    private int XOffsetFromServer;
    private int YOffsetFromServer;
    
    
    private boolean coordinatesUpdated;
    
    
    public OtherPlayer(String username){
        super();
        coordinatesUpdated = false;
        XfromServer = 0;
        YfromServer = 0;
        XOffsetFromServer = 0;
        YOffsetFromServer = 0;
        setUsername(username);
    }
    
    @Override
    public void update(float delta){
        
        //System.out.println(playerName + " is at " + getX() + "  " + getY());
        
        if(coordinatesUpdated){
            //System.out.println(playerName + " is moving!");
            listOfKeysPressed.clear();
            int currentX = ((int) getX()) * 32 + getXOffset();
            int currentY = ((int) getY()) * 32 + getYOffset();
            
            int serverX = XfromServer * 32 + XOffsetFromServer;
            int serverY = YfromServer * 32 + YOffsetFromServer;
            
            if (currentX < serverX) {
                addKeyPress(Player.RIGHT_PRESSED);
            } else if (currentX > serverX) {
                addKeyPress(Player.LEFT_PRESSED);
            }
            
            if (currentY< serverY) {
                addKeyPress(Player.DOWN_PRESSED);
            } else if (currentY > serverY) {
                addKeyPress(Player.UP_PRESSED);
            }
            
            // jump to server coords if the current coords
            // are too far behind (if the clients idea of where this 
            // player is has lagged too far behind, or if this otherPlayer has just spawned)
            if((currentX - serverX)*(currentX - serverX) > 16300 //(about four tiles)
                    || (currentY - serverY)*(currentY - serverY) > 16300 ){
                setCoords(XfromServer, YfromServer);
            }
            
            
            
            coordinatesUpdated = false;
        }
        if(XfromServer == getX() && XOffsetFromServer == getXOffset()){
            removeKeyPress(Player.RIGHT_PRESSED);
            removeKeyPress(Player.LEFT_PRESSED);
        }
        if(YfromServer == getY() && YOffsetFromServer == getYOffset()){
            removeKeyPress(Player.DOWN_PRESSED);
            removeKeyPress(Player.UP_PRESSED);
        }
        
        updateDirection();
        super.update(delta);
    }
    
    
    public void setXfromServer(int x){
        XfromServer = x;
        coordinatesUpdated = true;
    }
    
    public void setYfromServer(int y){
        YfromServer = y;
        coordinatesUpdated = true;
    }
    
    public void setXOffsetFromServer(int x){
        XOffsetFromServer = x;
        coordinatesUpdated = true;
    }
    
    public void setYOffsetFromServer(int y){
        YOffsetFromServer = y;
        coordinatesUpdated = true;
    }
    
}
