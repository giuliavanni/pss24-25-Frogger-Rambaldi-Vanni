package it.unibo.samplejavafx.core;

import it.unibo.samplejavafx.controller.ViewObserver;
import it.unibo.samplejavafx.controller.CollisionDetector;
import it.unibo.samplejavafx.view.MatchView;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

public class Match implements ViewObserver {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int LANE_HEIGHT = HEIGHT / 13; // 13 lanes in total
    private static final int OBSTACLE_WIDTH = 50; // Assuming each obstacle is 50 pixels wide
    private static final int LOG_WIDTH = 100; // Assuming each log is 100 pixels wide

    private Frog frog;
    private List<GameObjectNotControllable> objects;
    private List<Lane> lanes;
    private MatchView view;
    private CollisionDetector collisionDetector;
    private boolean isPaused = false;
    private AnimationTimer gameLoop;

    public Match(MatchView view) {
        this.view = view;
        this.collisionDetector = new CollisionDetector();
        this.objects = new ArrayList<>();
        this.lanes = new ArrayList<>();
        setupGame();
    }

    public Frog getFrog() {
        return frog;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void setupGame() {
        // Initialize Frog
        frog = new Frog(WIDTH / 2, HEIGHT - 50, 3);
    
        // Create ground lane (start)
        Lane groundStartLane = new Lane(0, 0, new ArrayList<>());
        lanes.add(groundStartLane);

        // Create log lanes and add logs to them
        for (int i = 0; i < 5; i++) {
            int direction = (i % 2 == 0) ? 1 : -1;
            Lane lane = new Lane((int) (Math.random() * 3 + 2), direction, new ArrayList<>());
            lanes.add(lane);
    
            // Add logs to lanes
            for (int j = 0; j < 3; j++) {
                int xPosition;
                boolean overlap;
                do {
                    overlap = false;
                    xPosition = (int) (Math.random() * (WIDTH - LOG_WIDTH));
                    for (GameObjectNotControllable obj : lane.getObjects()) {
                        if (Math.abs(obj.getXPosition() - xPosition) < LOG_WIDTH) {
                            overlap = true;
                            break;
                        }
                    }
                } while (overlap);
                int yPosition = (i + 1) * LANE_HEIGHT; // Position in the current lane
                lane.getObjects().add(new Log(xPosition, yPosition, lane.getSpeed()));
            }
        }        
    
        // Create ground lane (middle)
        Lane groundMiddleLane = new Lane(0, 0, new ArrayList<>());
        lanes.add(groundMiddleLane);
    
        // Create traffic lanes and add obstacles to them
        for (int i = 0; i < 5; i++) {
            int direction = (i % 2 == 0) ? 1 : -1;
            Lane lane = new Lane((int) (Math.random() * 3 + 2), direction, new ArrayList<>());
            lanes.add(lane);
    
            // Add obstacles to lanes
            for (int j = 0; j < 3; j++) {
                int xPosition;
                boolean overlap;
                do {
                    overlap = false;
                    xPosition = (int) (Math.random() * (WIDTH - OBSTACLE_WIDTH));
                    for (GameObjectNotControllable obj : lane.getObjects()) {
                        if (Math.abs(obj.getXPosition() - xPosition) < OBSTACLE_WIDTH) {
                            overlap = true;
                            break;
                        }
                    }
                } while (overlap);
                int yPosition = (i + 7) * LANE_HEIGHT; // Position in the current lane
                lane.getObjects().add(new Obstacle(xPosition, yPosition));
            }
        }
    
        // Create ground lane (end)
        Lane groundEndLane = new Lane(0, 0, new ArrayList<>());
        lanes.add(groundEndLane);
    
        // Create token in valid positions
        addTokenInValidPosition();

        startGameLoop();
    }

    private void addTokenInValidPosition() {
        // Choose a random lane index (excluding the ground lanes)
        int laneIndex = (int) (Math.random() * 10) + 1; // Lanes 1 to 10

        // Choose a random position within the lane, aligned with the frog's step size
        int xPosition = ((int) (Math.random() * (WIDTH / LANE_HEIGHT))) * LANE_HEIGHT;
        int yPosition = laneIndex * LANE_HEIGHT;

        // Add the token to the objects list
        objects.add(new Token(xPosition, yPosition));
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    update();
                }
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        for (Lane lane : lanes) {
            lane.updateObjectsPosition();
        }
        collisionDetector.handleCollisions(frog, objects);
        frog.updatePosition(); // Update frog's position if on a log
    }

    private void render() {
        view.clearScene(); // Clear the scene before rendering
        
        // Render lanes and other elements first
        view.renderGroundLane(lanes.get(0), 0);

        for (int i = 1; i <= 5; i++) {
            view.renderLogLane(lanes.get(i), i);
        }

        view.renderGroundLane(lanes.get(6), 6);

        for (int i = 7; i <= 11; i++) {
            view.renderTrafficLane(lanes.get(i), i);
        }

        view.renderGroundLane(lanes.get(12), 12);

        view.renderFrog(frog);
        
        for (GameObjectNotControllable token : objects) {
            view.renderToken(token);
        }
        
        // Draw lines last so they appear on top
        view.drawLaneLines();
        
        view.updateFrogPosition(frog);
    }

    @Override
    public void handleInput(KeyCode code) {
        frog.move(code);
    }

    @Override
    public void updateView() {
        render();
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public void stop() {
        gameLoop.stop();
    }
}