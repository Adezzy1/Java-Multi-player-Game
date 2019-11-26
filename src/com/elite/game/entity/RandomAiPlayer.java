package com.elite.game.entity;

import com.elite.framework.misc.RandomNG;
import com.elite.game.assets.Bitmaps;

public class RandomAiPlayer extends Player {

    private float directionTimer = 1f;
    private float speedMultiplier = 0.5f;
    
    public RandomAiPlayer(byte x, byte y){
        super();
        this.x = x;
        this.y = y;
        
        int r = RandomNG.getRandInt(Bitmaps.player_model_names.size());
        this.setPlayerModel(Bitmaps.player_model_names.get(r));
        
    }
    
    @Override
      public void addKeyPress(int keyPress){
        if(!listOfKeysPressed.contains(keyPress)){ //Do nothing
            listOfKeysPressed.add(0, keyPress);
        }
    }
    
    @Override
    public void update(float delta) {
        super.update(delta * speedMultiplier);
        directionTimer -= delta;
        if(directionTimer < 0) {
            directionTimer = 1;
            switch(RandomNG.getRandInt(5)) {
            case 0:
                setFacingUp();
                listOfKeysPressed.clear();
                addKeyPress(Player.UP_PRESSED);
                break;
            case 1:
                setFacingLeft();
                listOfKeysPressed.clear();
                addKeyPress(Player.LEFT_PRESSED);
                break;
            case 2:
                setFacingRight();
                listOfKeysPressed.clear();
                addKeyPress(Player.RIGHT_PRESSED);
                break;
            case 3:
                setFacingDown();
                listOfKeysPressed.clear();
                addKeyPress(Player.DOWN_PRESSED);
                break;  
            case 4:
                // stop moving and stand still for a bit
                listOfKeysPressed.clear();
                break;
                
            }
        }
        
    }
    
    
    
}
