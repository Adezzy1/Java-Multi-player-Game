package com.elite.game.client;

import com.elite.game.assets.Sounds;
import com.elite.game.entity.Announcement;
import com.elite.game.entity.Map;
import com.elite.game.entity.OtherPlayer;
import com.elite.game.state.GameOverState;
import com.elite.game.state.PlayState;
import com.elite.game.weapons.Flamethrower;
import com.elite.game.weapons.Pistol;
import com.elite.game.weapons.Shotgun;
import com.elite.game.weapons.SniperRifle;
import com.elite.game.weapons.Weapon;

/**
 * The behaviour in this class is part of the <b>PlayState</b> update method/loop
 * but has been refactored out into it's own class to stop <b>PlayState</b> becoming too large.
 * 
 *  <p> This class is responsible for interpreting the TCP and UDP updates received from 
 *  the Server and applying them to the <b>PlayState</b> that created this update handler.
 * 
 * @author Fraser Brooks
 * @author Sabeeka Qureshi
 * @author Beenita Shah
 *
 */
public class ServerUpdateHandler {

    private PlayState playState;
    
    public ServerUpdateHandler(PlayState ps){
        playState = ps;
    }
    
    public void handleTCP(String update) {
        System.out.println(update);
        if (update.matches(".*PickedUp.*")) {
            pickUpWeaponUpdate(update);
        }else if(update.matches(".*weaponDrop.*")){
            weaponDropUpdate(update);
        }else if(update.matches(".*disconnect.*")){
            playerDisconnectedUpdate(update);
        }else if(update.matches(".*discovered.*")){
            bodyDiscoveredUpdate(update);
        }else if(update.matches(".*gameover.*")){
            gameOverUpdate(update);
        }
    }
    
    public void handleUDP(byte[] update) {
        String fromServer = new String(update).trim();
        String[] parts = fromServer.split(">");
        String playerName = parts[0].split(":")[1];
        if(parts[1].equals("move")){
            for(OtherPlayer op: playState.getGameLobby().getOtherPlayers()){
                if(op.getUsername().equals(playerName)){
                    op.setXfromServer(Integer.parseInt(parts[2]));
                    op.setYfromServer(Integer.parseInt(parts[3]));
                    op.setXOffsetFromServer(Integer.parseInt(parts[4]));
                    op.setYOffsetFromServer(Integer.parseInt(parts[5]));
                }
            }
        }else if(parts[1].equals("shoot")){
            for(OtherPlayer op: playState.getGameLobby().getOtherPlayers()){
                if(op.getUsername().equals(playerName)){
                    System.out.println(playerName + " shot");
                    playState.getShootHandler().playerShoot(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), op);
                    
                    //networkwrapper.(send TCP)
                }
            }
        }else if(parts[2].equals("shotFor")){
            String shotPlayer = parts[1];
            int damage = Integer.parseInt(parts[3]);
            if(playState.getPlayer().getUsername().equals(shotPlayer)){
                playState.getPlayer().addHealth(-damage);
            }
            System.out.println("shot For>" + shotPlayer);
            for(OtherPlayer op: playState.getOtherPlayers()){
                if(op.getUsername().equals(shotPlayer)){
                    op.addHealth(-damage);
                }
            }
            System.out.println(shotPlayer + " shot for " + damage);
        }else{
            System.err.println("Error: unrecognized UDP:" + fromServer);
        }
    }
    
    private void gameOverUpdate(String update) {
        String winningTeam = update.split("gameover~")[1];
        playState.setCurrentState(new GameOverState(
                winningTeam, playState.getGameLobby()));
    }

    
    private void bodyDiscoveredUpdate(String update) {
        System.out.println("someone dead");
        String message = update.split(">>")[1];
        String discoverer = message.split("~")[0];
        String deadPlayer = message.split("~")[2];
        String team =  playState.getPlayer().isSpy() ? "a devious spy!" : "just a guy.";
        for(OtherPlayer op :  playState.getOtherPlayers()){
            if(op.getUsername().equals(deadPlayer)){
                team = op.isSpy() ? "a  devious spy!" : "just a guy.";
            }
        }
        if( playState.getAnnouncements().size() > 6){
            playState.getAnnouncements().clear();
        }
        playState.getAnnouncements().add(new Announcement(
                discoverer + " found the body of " 
        + deadPlayer + " they were " + team));
    }

    
    private void playerDisconnectedUpdate(String update) {
        String playerThatQuit = update.split("disconnect:")[1];
        for (OtherPlayer op :  playState.getOtherPlayers()){
            if (op.getUsername().equals(playerThatQuit)){
                op.addHealth(-99999);
            }
        }
    }

    
    private void weaponDropUpdate(String update) {
        String[] parts = update.split(">");
        String username = parts[0].split(":")[1];
        int weaponID = Integer.parseInt(parts[2]);
        int x = Integer.parseInt(parts[3]);
        int y = Integer.parseInt(parts[4]);
        switch(weaponID){
        case Map.PISTOL_ID:
            playState.getWeapons().add(new Pistol(x,y));
            break;
        case Map.SHOTGUN_ID:
            playState.getWeapons().add(new Shotgun(x, y));
            break;
        case Map.FLAME_ID:
            playState.getWeapons().add(new Flamethrower(x, y));
            break;
        case Map.SNIPER_ID:
            playState.getWeapons().add(new SniperRifle(x, y));
            break;
        default:
                System.out.println("Error: Unrecognized weaponID");
        }
        for (OtherPlayer op : playState.getOtherPlayers()){
            if (op.getUsername().equals(username)){
                op.setEquipedWeapon(null);
            }
        }
    }

    
    private void pickUpWeaponUpdate(String update) {
        String message = update.split(":")[1];
        String[] updateParts = message.split(">>");
        String username = updateParts[0];
        String weaponInfo = updateParts[2];
        int weaponID = Integer.parseInt(weaponInfo.split("at")[0]);
        Sounds.playGuncock();
        Weapon pickedUpWeapon = null;
        switch (weaponID) {
        case Map.PISTOL_ID:
            pickedUpWeapon = new Pistol(999, 999);
            break;
        case Map.SHOTGUN_ID:
            pickedUpWeapon = new Shotgun(999, 999);
            break;
        case Map.FLAME_ID:
            pickedUpWeapon = new Flamethrower(999, 999);
            break;
        case Map.SNIPER_ID:
            pickedUpWeapon = new SniperRifle(999, 999);
            break;
        default:
            pickedUpWeapon = new Pistol(999, 999);
            break;
        }
        if (username.equals(NetworkWrapper.username)) {
            playState.getPlayer().pickupWeapon(pickedUpWeapon);
        } else {
            for (OtherPlayer op : playState.getOtherPlayers()) {
                if (op.getUsername().equals(username)) {
                    op.pickupWeapon(pickedUpWeapon);
                }
            }
        }
        
        String[] coords = weaponInfo.split("at")[1].split("~");
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        int numberOfWeapons = playState.getWeapons().size();
        for(int i = 0; i < numberOfWeapons; i++){
            Weapon w = playState.getWeapons().get(i);
            if(w.getX() == x && w.getY() == y){
                playState.getWeapons().remove(i);
                break;
            }
        }
    }
    
}
