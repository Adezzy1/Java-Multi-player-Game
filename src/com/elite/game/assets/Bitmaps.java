package com.elite.game.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.elite.framework.animation.Animation;
import com.elite.framework.animation.Frame;


/**
 * Class for loading all Bitmaps needed by the game into memory. 
 * 
 * <p> All player model bitmaps and Animations are loaded into two nested Hashtables
 *  to make access easier and to prevent the need for over a hundred different BufferedImages
 *  being listed below. The Bitmaps pertaining to each individual weapon are loaded in a similar
 *  fashion.</p>
 *  
 *  <p>Any Bitmaps needed throughout the whole game are loaded when the game is launched.
 *  The Bitmaps needed for any particular map (i.e. all the tiles) are loaded with the loadTiles
 *  function but this does not happen until the host has picked a map. These are also loaded
 *  into a Hashtable where the Integer key represents the tile code that is specified in the
 *  file name that determines whether that tile is collidable or not. </p>
 * 
 * @author Fraser Brooks
 * @author Sabeeka Qureshi
 * @author Beenita Shah
 * @author Adeola Aderibigbe
 */
public class Bitmaps {

    
    // HashTable to store map tiles
    public static Hashtable<Integer, BufferedImage> tiles;
    
    // HashTable to store the player Model bitmaps and animations
    // All Keys in one should be present in the other
    public static Hashtable<String, Hashtable<String, BufferedImage>> player_model_imgs;
    public static Hashtable<String, Hashtable<String, Animation>> player_model_anims;
    
    // Arraylist containing the keys for the HashTables above
    public static ArrayList<String> player_model_names = new ArrayList<>();
    
    
    //HashTable to store Weapons
    public static Hashtable<String, BufferedImage> weaponBitmaps;
    
    // All the Bitmaps that aren't sorted into a collection
    public static BufferedImage 
    welcome, 
    iconimage,
    creategame,
    joingame,
    rulesbutton,
    quitgame,
    quitbox,
    noquit,
    yesquit,
    creategamebox,
    creategameusernamebox,
    creategamekeybox,
    submit,
    textbox,
    cross,
    nullTile;
    
   // Time between player model animation frames. 
    private static float fT = 0.2f; 
    
    public static void load(){
        welcome = loadImage("welcome.png");
        iconimage = loadImage("iconimage.png");
        creategame = loadImage("creategame.png");
        joingame = loadImage("joingame.png");
        rulesbutton = loadImage("rulesbutton.png");
        quitgame = loadImage("quitgame.png");
        quitbox = loadImage("quitbox.png");
        noquit = loadImage("noQuit.png");
        yesquit = loadImage("yesQuit.png");
        creategamebox = loadImage("creategamebox.png");
        creategameusernamebox = loadImage("creategameusernamebox.png");
        creategamekeybox = loadImage("creategamekeybox.png");
        submit = loadImage("submit.png");
        textbox = loadImage("textbox.png");
        cross = loadImage("cross.png");
        
        nullTile = loadImage("background.png");
        
        loadWeapons();
        
        
        loadPlayerModels();
        
    }

    /**
     * load tiles from ./src/assets/maps/mapName folder
     * and store them in the tiles Hashtable
     * @param mapName - the name of the map/ directory to load
     */
    public static void loadTiles(String mapName){
        
        tiles = new Hashtable<>();
        System.out.println(mapName);
        File tile_folder = new File("./src/assets/maps/" + mapName);
        File[] listOfFiles = tile_folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        
        
        int i = 0;
        for(File f : listOfFiles){
            if(f.getName().matches(".*.csv")){
                fileNames[i] = "";
                i++;
                continue;
            }
            fileNames[i] = f.getName();
            i++;
        }
        
        for (int k = 0; k < fileNames.length; k++) {
            if (fileNames[k].equals("")) {
                continue;
            }
            if ((!fileNames[k].matches("-?\\d+_.*"))) {
                System.err.println("Error bad filename: " + fileNames[k]);
                continue;
            }
            int tileNumber = Integer.parseInt(fileNames[k].split("_")[0]);
            BufferedImage img = loadImage(fileNames[k], "/assets/maps/" + mapName + "/");
            if (img != null) {
                tiles.put(tileNumber, img);
            }
        }
        
        
    }
    
    public static void loadPlayerModels(){
        
        player_model_imgs = new Hashtable<>();
        player_model_anims = new Hashtable<>();
        
        File player_models_folder = new File("./src/assets/bitmaps/player_models");
        File[] listOfFiles = player_models_folder.listFiles();
        
        for(File f : listOfFiles){
            if(!f.isDirectory()){
                continue;
            }
            String model_name = f.getName();
            player_model_names.add(model_name);
            Hashtable<String, BufferedImage> images = getPlayerModelImages(model_name);
            player_model_imgs.put(model_name, images);
            Hashtable<String, Animation> animations = getPlayerModelAnimations(model_name);
            player_model_anims.put(model_name, animations);
        }
        
    }
    
    public static BufferedImage getPlayerImage(String model_name, String part_name){
        return player_model_imgs.get(model_name).get(part_name);
    }
    
    public static Animation getPlayerAnim(String model_name, String anim_name){
        return player_model_anims.get(model_name).get(anim_name);
    }
    
    
    /**
     * load the character model Bitmaps that are needed for Animations and create 
     * the required Animation objects for them then store them in the animations Hashtable.
     * @param model_name
     * @return
     */
    private static Hashtable<String, Animation> getPlayerModelAnimations(String model_name) {
        Hashtable<String, Animation> animations = new Hashtable<>();
        
        File player_model_dir = new File("./src/assets/bitmaps/player_models/" + model_name);
        File[] listOfFiles = player_model_dir.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        
        int i = 0;
        for(File f : listOfFiles){
            if(!f.getName().matches("player_" + model_name + "_.*\\.png")){
                fileNames[i] = "";
                i++;
                continue;
            }
            fileNames[i] = f.getName();
            i++;
        }
        
        Hashtable<String, BufferedImage> imgs = new Hashtable<>();
        
        for (int k = 0; k < fileNames.length; k++) {
            if (fileNames[k].equals("")) {
                continue;
            }
            String model_part = fileNames[k].split(model_name + "_")[1].split("\\.png")[0];
            if(!(model_part.matches(".*three") || model_part.matches(".*dead") || model_part.matches(".*ui"))){
                BufferedImage img = loadImage(fileNames[k], 
                        "/assets/bitmaps/player_models/" + model_name + "/");
                imgs.put(model_part, img);
            }
        }
        
        Frame u1 = new Frame(imgs.get("back_one"), fT);
        Frame u2 = new Frame(imgs.get("back_two"), fT);
        
        Frame l1 = new Frame(imgs.get("left_one"), fT);
        Frame l2  = new Frame(imgs.get("left_two"), fT);
        
        Frame r1 = new Frame(imgs.get("right_one"), fT);
        Frame r2 = new Frame(imgs.get("right_two"), fT);
        
        Frame d1 = new Frame(imgs.get("front_one"), fT);
        Frame d2 = new Frame(imgs.get("front_two"), fT);
        
        Animation upAnimation = new Animation(u1, u2);
        Animation leftAnimation = new Animation(l1, l2);
        Animation rightAnimation = new Animation(r1, r2);
        Animation downAnimation = new Animation(d1, d2);
        
        animations.put("up", upAnimation);
        animations.put("left", leftAnimation);
        animations.put("right", rightAnimation);
        animations.put("down", downAnimation);
        
        return animations;
    }

    private static Hashtable<String, BufferedImage> getPlayerModelImages(String model_name){
        
        Hashtable<String, BufferedImage> images = new Hashtable<>();
        
        File player_model_dir = new File("./src/assets/bitmaps/player_models/" + model_name);
        File[] listOfFiles = player_model_dir.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        
        int i = 0;
        for(File f : listOfFiles){
            if(!f.getName().matches("player_" + model_name + "_.*\\.png")){
                fileNames[i] = "";
                i++;
                continue;
            }
            fileNames[i] = f.getName();
            i++;
        }
        
        for (int k = 0; k < fileNames.length; k++) {
            if (fileNames[k].equals("")) {
                continue;
            }
            String model_part = fileNames[k].split(model_name + "_")[1].split("\\.png")[0];
            if(model_part.matches(".*three") || model_part.matches(".*dead") || model_part.matches(".*ui") ){
                BufferedImage img = loadImage(fileNames[k], 
                        "/assets/bitmaps/player_models/" + model_name + "/");
                images.put(model_part, img);
            }
        }
        
        return images;
    }
    
    /**
     * get the BufferedImage of the tile with tile code i
     * @param i
     * @return - the BufferefImage of the tile which should be 32x32 pixels
     */
    public static BufferedImage getTile(int i){
        BufferedImage img = tiles.get(i);
        if(img != null){
            return img;
        }else{
            return nullTile;
        }
    }
    
    
    /**
     * Load all the weapon Bitmaps
     */
    private static void loadWeapons() {
        weaponBitmaps = new Hashtable<>();
        
        String weaponDir = "weapon_models/";
        String[] weapons = {"flamethrower", "pistol", "shotgun", "sniper"};
        String[] allBitmaps = {"front", "frontF", "back", "backF", "left", "leftF",
                                                       "right", "rightF", "flat", "projectile", "hitParticle"};
        
        for(String weapon : weapons){
            for(String bmap : allBitmaps){
                weaponBitmaps.put(weapon + "_" + bmap, 
                        loadImage(weaponDir + weapon + "/" + weapon + "_" + bmap + ".png"));
                System.out.println("loaded " + weapon + "_" + bmap);
            }
        }
        
    }
    
    public static BufferedImage getWeaponImage(String weapon, String bmap){
        return weaponBitmaps.get(weapon + "_" + bmap);
    }
    
    /**
     * load an image from the /assets/bitmaps/ folder with the file name 'filename'
     * @param filename
     * @return - the BufferedImage loaded or null if the image couldn't be loaded
     */
    private static BufferedImage loadImage(String filename) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(Assets.class.getResourceAsStream("/assets/bitmaps/" + filename));
        } catch (IOException e) {
            System.err.println("Error while reading image file: /resources/" + filename);
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.err.println("Bad file: " + filename );
        }

        return img;
    }
    
    /**
     * load an image from the folder specified by 'path' with the file name 'filename'
     * @param filename
     * @param path
     * @return - the BufferedImage loaded or null if the image couldn't be loaded
     */
    private static BufferedImage loadImage(String filename, String path) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(Assets.class.getResourceAsStream(path + filename));
        } catch (IOException e) {
            System.err.println("Error while reading image file: /resources/" + filename);
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.err.println("Error bad file: " + path + filename );
        }

        return img;
    }
    
    
    
    
}
