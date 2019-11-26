package com.elite.game.engine;

import com.elite.game.client.NetworkWrapper;
import com.elite.game.entity.HitParticle;
import com.elite.game.entity.OtherPlayer;
import com.elite.game.entity.Particle;
import com.elite.game.entity.Player;
import com.elite.game.state.PlayState;
import com.elite.game.weapons.Weapon;

/**
 * This class is part of the <b>PlayState</b> logic,
 * 
 * <p> It processes the player clicking to shoot another player and the
 * server letting us know that one of the other players has shot.</p>
 * 
 * @author Fraser Brooks
 *
 */
public class ShootHandler {

    private PlayState playState;
    
    public ShootHandler(PlayState ps){
        playState = ps;
    }
    
    /**
     * Called when any one player tries to shoot somewhere.
     * Calls to this method either come from this client clicking somewhere
     * on the screen while a weapon is equiped or from the server letting us
     * know that another player has shot and the coordinates they shot at.
     * 
     * Note: the weapon will only fire if the gun has cooled down since the
     * last shot, else the method will simply return false.
     * 
     * @param clickX - the X coordinate the shot was directed at
     * @param clickY - the Y coordinate the shot was directed at
     * @param p - the player that is trying t 
     * @return true if the weapon fired else false if it was still cooling down
     */
    public boolean playerShoot(int clickX, int clickY, Player p) {
        if(p.getWeapon() != null){
              if( p.getWeapon().shoot()){
                  
                  int[] universalPlayer = CoordinateConverter.universalCoords(
                          p.getX(),
                          p.getY(),
                          p.getXOffset(),
                          p.getYOffset());
                  
                  turnPlayer(p, clickX, clickY, 
                          universalPlayer[0], universalPlayer[1]);
                  
                  int[] path = Weapon.projectilePath(
                          universalPlayer[0] + 16,
                          universalPlayer[1] + 16, 
                          clickX,
                          clickY,
                          Weapon.PROJECTILE_ACCURACY,
                          p.getWeapon().getRange());
                  
                  boolean collided = false;
                  
                  int xHit = 0;
                  int yHit = 0;
                  
                  for(int i = 0; i < path.length - 1; i += 2){
                      if(collided){
                          path[i] = 10000;
                          path[i+1] = 10000;
                      }else{
                          int[] tileCoords = CoordinateConverter.tileCoords(path[i], path[i+1]);
                          if(playState.getMap().isWall(tileCoords[0], tileCoords[1]) 
                                  || bulletCollisionAt(path[i], path[i+1], p)){
                              collided = true;
                              xHit = path[i];
                              yHit = path[i+1];
                          }
                      }
                  }
                  if(collided){
                      playState.getParticles().add(new HitParticle(xHit, yHit, p.getWeapon().hitParticle));
                  }
                  playState.getParticles().add(new Particle(path, p.getWeapon().projectile));
                  return true;
              }
          }
        return false;
    }

    
    /**
     * Turn the player to face in the direction they shot in to stop players
     * seemingly shooting bullets out of the back of their head while running away
     * @param p - the player that shot their gun
     * @param clickX - the X coordinate the shot was directed at
     * @param clickY - the Y coordinate the shot was directed at
     * @param pX - the X coordinate of the shooter
     * @param pY - the Y coordinate of the shooter
     */
    private void turnPlayer(Player p, int clickX, int clickY, int pX, int pY) {

        int eastDist = CoordinateConverter.distance(clickX, clickY, pX + 300, pY);
        int westDist = CoordinateConverter.distance(clickX, clickY, pX - 300, pY);
        int northDist = CoordinateConverter.distance(clickX, clickY, pX, pY - 300);
        int southDist = CoordinateConverter.distance(clickX, clickY, pX, pY + 300);

        p.resetDirectionCount();
        
        // work out which point is closest to the click
        if (eastDist < westDist && eastDist < northDist && eastDist < southDist) {
            p.setFacingRight();
        } else if (westDist < eastDist && westDist < northDist && westDist < southDist) {
            p.setFacingLeft();
        } else if (northDist < westDist && northDist < eastDist && northDist < southDist) {
            p.setFacingUp();
        } else {
            p.setFacingDown();
        }
        
    }
    
    /**
     * Calculate if there's been a bullet collision and calculate the damage dealt
     * and inform the server if necessary.
     * @param bulletX - the X coordinate of the bullet/projectile
     * @param bulletY- the Y coordinate of the bullet/projectile
     * @param shooter - the shooter
     * @return - true if there was a bullet collision signalling that the bullet should stop travelling
     */
    private boolean bulletCollisionAt(int bulletX, int bulletY, Player shooter) {

        if(!shooter.getUsername().equals(playState.getPlayer().getUsername())){
            System.out.println("someone else has shot");
            int[] playerCoords = CoordinateConverter.universalCoords(
                    playState.getPlayer().getX(), 
                    playState.getPlayer().getY(),
                    playState.getPlayer().getXOffset(), 
                    playState.getPlayer().getYOffset()); 
            if(playerCoords[0] < bulletX && playerCoords[0] + 32 > bulletX
                    && playerCoords[1] < bulletY && playerCoords[1] + 32 > bulletY){
                System.out.println("someone else has shot you ");
                int[] shooterCoords = CoordinateConverter.universalCoords(
                        shooter.getX(), shooter.getY(), 
                        shooter.getXOffset(), shooter.getYOffset());
                int dist = CoordinateConverter.distance(shooterCoords[0], 
                                                                        shooterCoords[1] , 
                                                                        playerCoords[0],
                                                                        playerCoords[1]);
                int damage = shooter.getWeapon().damage(dist);
                NetworkWrapper.sendUDP((">" + playState.getPlayer().getUsername() + 
                        ">shotFor>" + damage).getBytes());
                playState.getPlayer().addHealth(-damage);
                return true;
            }
        }
        
        for(int i = 0; i < playState.getOtherPlayers().size(); i++){
            OtherPlayer p = playState.getOtherPlayers().get(i);
            if(p.getUsername().equals(shooter.getUsername())){
                continue;
            }
            int[] universalCoords = CoordinateConverter.universalCoords(p.getX(), p.getY(),
                    p.getXOffset(), p.getYOffset()); 
            if(universalCoords[0] < bulletX && universalCoords[0] + 32 > bulletX
                    && universalCoords[1] < bulletY && universalCoords[1] + 32 > bulletY){
                return true;
            }
        }
        
        return false;
    }
    
    
    
}
