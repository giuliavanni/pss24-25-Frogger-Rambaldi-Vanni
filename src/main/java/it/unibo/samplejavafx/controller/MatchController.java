package it.unibo.samplejavafx.controller;

import it.unibo.samplejavafx.core.*;
import it.unibo.samplejavafx.view.MatchView;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MatchController implements ViewObserver {

    private Frog frog;
    private List<Lane> lanes;
    private MatchView view;
    private CollisionDetector collisionDetector;
    private boolean isPaused;
    private AnimationTimer gameLoop;
    private List<GameObjectNotControllable> objects;
    private MediaPlayer musicPlayer;
    private boolean isMusicPlaying = false;

    public MatchController(Frog frog, List<Lane> lanes, List<GameObjectNotControllable> objects, MatchView view) {
        this.frog = frog;
        this.lanes = lanes;
        this.objects = objects;
        this.view = view;
        this.collisionDetector = new CollisionDetector();
        this.isPaused = false;
        SoundManager.loadSoundEffects();
        initializeMusic();
        startGameLoop();
    }

    private void initializeMusic() {
        try {
            String musicFile = "/Frogger_Theme.mp3";
            Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
            musicPlayer = new MediaPlayer(sound);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop music
            playMusic();
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
        }
    }
    
    public void playMusic() {
        if (musicPlayer != null && !isMusicPlaying) {
            musicPlayer.play();
            isMusicPlaying = true;
        }
    }
    
    public void pauseMusic() {
        if (musicPlayer != null && isMusicPlaying) {
            musicPlayer.pause();
            isMusicPlaying = false;
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    update();
                }
                updateView();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // Update game state
        for (Lane lane : lanes) {
            lane.updateObjectsPosition();
        }
        
        // Check collisions with lane objects
        for (Lane lane : lanes) {
            collisionDetector.handleCollisions(frog, lane.getObjects());
        }
    
        // Check collisions with tokens and other objects not in lanes
        collisionDetector.handleCollisions(frog, objects);
        
        // Update frog position if on log
        frog.updatePosition();
    
        // Check game over conditions
        if (frog.getLives() <= 0) {
            gameOver();
        }
    }

    @Override
    public void handleInput(KeyCode code) {
        if (code == KeyCode.P) {
            togglePause();
        } else {
            frog.move(code);
        }
    }

    @Override
    public void updateView() {
        if (frog.getLives() <= 0) {
            view.renderGameOver(calculateScore());
            return;
        }

        view.clearScene();
        
        // Render lanes
        for (int i = 0; i < lanes.size(); i++) {
            Lane lane = lanes.get(i);
            if (i == 0 || i == 6 || i == 12) {
                view.renderGroundLane(lane, i);
            } else if (i >= 1 && i <= 5) {
                view.renderLogLane(lane, i);
            } else {
                view.renderTrafficLane(lane, i);
            }
        }
        
        // Render tokens using objects list
        for (GameObjectNotControllable obj : objects) {
            if (obj instanceof Token) {
                view.renderToken(obj);
            }
        }
        
        view.renderFrog(frog);
        view.renderLives(frog);
        view.drawLaneLines();
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    private void gameOver() {
        stop();
        view.renderGameOver(calculateScore());
    }

    public void stop() {
        gameLoop.stop();
    }

    private int calculateScore() {
        // Implement score calculation logic
        return 0;
    }
}