package com.elite.game.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import com.elite.framework.misc.InputWrapper;
import com.elite.game.state.LoadState;
import com.elite.game.state.State;



/**
 * This is the container and starting point of the game control flow. 
 * 
 * <p> Control flow starts from here inside the run method and for 
 * each frame, the control flow descends into the currentStates update method
 * passing in the time passed in seconds (delta) since the last update method (obviously
 * a very small number). The currentState is then responsible for updating anything
 * that needs to be updated with this delta value (player movement, animations, bullets etc.)  
 * and then once the control flow returns all the way back to this class the currentStates
 *  render method is called passing a Graphics object of a fixed size for the next frame to be
 *  rendered on. Once the render method finally returns, the Graphics object that
 *  was passed down is drawn onto this JPanel, stretching it as necessary.</p>
 * 
 * @author Fraser Brooks
 */
@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable{
    
    public static final int GAME_WIDTH = Renderer.TILES_ACROSS * Renderer.TILE_SIZE; 
    public static final int GAME_HEIGHT = Renderer.TILES_DOWN * Renderer.TILE_SIZE;
    
    private int windowWidth = 1600;
    private int windowHeight = 900;
    
    private Image gameImage;
    
    private volatile State currentState;
    
    private InputWrapper inputWrapper;
    
    private Thread gameThread;
    private volatile boolean running;
    
    public Game(){
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocus();
    }
    
    public void setCurrentState(State newState){
        System.gc();
        newState.init();
        currentState = newState;
        inputWrapper.setCurrentState(newState);
    }
    
    @Override
    public void addNotify(){
        super.addNotify();
        initInput();
        setCurrentState(new LoadState());
        initGame();
    }
    
    private void initGame(){

        running = true;
        gameThread = new Thread(this, "Game Thread");
        gameThread.start();
    }

    @Override
    public void run() {
        
        long updateDuration = 0; // measures both update and render
        long sleepDuration = 0; 
        // this and update should sum to 17 on each iteration because one
        // frame every 17 milliseconds roughly equals 60 frames per second
        
        while (running){
            long beforeUpdate = System.nanoTime();
            long deltaMillis = updateDuration + sleepDuration;
            
            updateAndRender(deltaMillis);

            updateDuration = (System.nanoTime() - beforeUpdate) / 1000000L;
            sleepDuration = Math.max(2, 17 - updateDuration);
            
            
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            
        }
        
        //End game when running is False
        System.exit(0);
        
    }

    private void updateAndRender(long deltaMillis) {
        // convert to seconds to allow denotion in pixels per second
        currentState.update(deltaMillis / 1000f); 
        //Double Buffering (reduce tearing)
        prepareGameImage();
        currentState.render(gameImage.getGraphics(), this.getWidth(), this.getHeight());
        renderGameImage(getGraphics());
    }
    
    public void exit(){
        running = false;
    }

    private void prepareGameImage() {
        if (gameImage == null){
            gameImage = createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        Graphics g = gameImage.getGraphics();
        //clear previous frame
        g.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
    }
    
    
    private void renderGameImage(Graphics g){
        if (gameImage != null){
            g.drawImage(gameImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
        g.dispose();
    }
    
    private void initInput() {
        inputWrapper = new InputWrapper();
        addKeyListener(inputWrapper);
        addMouseListener(inputWrapper);
    }
    
    
}
