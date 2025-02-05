package it.unibo.samplejavafx.view;

import it.unibo.samplejavafx.core.Frog;
import it.unibo.samplejavafx.core.Lane;
import it.unibo.samplejavafx.core.GameObjectNotControllable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import it.unibo.samplejavafx.main.MainApp;
import javafx.util.Duration;


public class MatchView {
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int LANE_HEIGHT = HEIGHT / 13;

    private Canvas canvas;
    private GraphicsContext gc;
    private Font pixelFont;
    private Stage stage;
    private MainApp mainApp;

    private ProgressBar timerBar;
    private static final int GAME_DURATION = 60;  // Durata del gioco in secondi
    private double timeLeft = GAME_DURATION;  // Tempo rimanente
    private AnchorPane rootPane;


    public MatchView(Stage stage, MainApp mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;
        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.pixelFont = Font.loadFont(getClass().getResourceAsStream("/PressStart2P-Regular.ttf"), 36);

        // Inizializza rootPane
        rootPane = new AnchorPane();

        // Crea la barra di progresso per il timer
        timerBar = new ProgressBar(1.0);  // Inizia al 100%
        timerBar.setPrefWidth(300); 
        timerBar.setPrefHeight(25);
        timerBar.setStyle("-fx-accent: green;");  // Imposta il colore della barra

        // Posiziona la barra in basso a destra
        AnchorPane.setBottomAnchor(timerBar, 10.0);
        AnchorPane.setRightAnchor(timerBar, 10.0);

        // Aggiungi il canvas al rootPane
        rootPane.getChildren().add(canvas);

        // Aggiungi la barra del timer al rootPane
        rootPane.getChildren().add(timerBar);  
        
        // Imposta la scena
        Scene scene = new Scene(rootPane, WIDTH, HEIGHT);  
        stage.setScene(scene);
        stage.show();

        // Avvia il timer
        startTimer();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void clearScene() {
        gc.setFill(Color.BLACK); // Set the background color
        gc.fillRect(0, 0, WIDTH, HEIGHT); // Clear the canvas with the background color
    }
 
    public void renderFrog(Frog frog) {
        gc.drawImage(frog.getImageView().getImage(), frog.getXPosition(), frog.getYPosition(), 40, 40);
    }

    public void renderGroundLane(Lane lane, int laneIndex) {
        gc.setFill(Color.PURPLE); // Set the background color to purple for ground lanes
        gc.fillRect(0, laneIndex * LANE_HEIGHT, WIDTH, LANE_HEIGHT); // Fill the lane with purple color
        for (GameObjectNotControllable obj : lane.getObjects()) {
            gc.drawImage(obj.getImageView().getImage(), obj.getXPosition(), obj.getYPosition(), obj.getImageView().getFitWidth(), obj.getImageView().getFitHeight());
        }
    }

    public void renderTrafficLane(Lane lane, int laneIndex) {
        gc.setFill(Color.BLACK); // Set the background color to black for traffic lanes
        gc.fillRect(0, laneIndex * LANE_HEIGHT, WIDTH, LANE_HEIGHT); // Fill the lane with black color
        for (GameObjectNotControllable obj : lane.getObjects()) {
            gc.drawImage(obj.getImageView().getImage(), obj.getXPosition(), obj.getYPosition(), obj.getImageView().getFitWidth(), obj.getImageView().getFitHeight());
        }
    }

    public void renderLogLane(Lane lane, int laneIndex) {
        gc.setFill(Color.BLUE); // Set the background color to blue for log lanes
        gc.fillRect(0, laneIndex * LANE_HEIGHT, WIDTH, LANE_HEIGHT); // Fill the lane with blue color
        for (GameObjectNotControllable obj : lane.getObjects()) {
            gc.drawImage(obj.getImageView().getImage(), obj.getXPosition(), obj.getYPosition(), obj.getImageView().getFitWidth(), obj.getImageView().getFitHeight());
        }
    }

    public void renderToken(GameObjectNotControllable token) {
        gc.drawImage(token.getImageView().getImage(), token.getXPosition(), token.getYPosition(), token.getImageView().getFitWidth(), token.getImageView().getFitHeight());
    }

    public void updateFrogPosition(Frog frog) {
        renderFrog(frog); // Render the frog at the new position
    }

    public void renderLives(Frog frog) {
        // Define size and position of lives icons
        final int LIFE_ICON_SIZE = 30;  // Smaller size for lives icons
        final int PADDING = 10;         // Space from border
        final int SPACING = 5;          // Space between icons
        final int BASE_Y = HEIGHT - LIFE_ICON_SIZE - PADDING;  // Base Y position
        
        // For each lives draw a Frog icon
        for (int i = 0; i < frog.getLives(); i++) {
            double x = PADDING + (i * (LIFE_ICON_SIZE + SPACING));
            gc.drawImage(frog.getImageView().getImage(), x, BASE_Y, LIFE_ICON_SIZE, LIFE_ICON_SIZE);
        }
    }

    public void renderGameOver(int score) {
        showGameOverScreen(score);
    }

    private void showGameOverScreen(int score) {
        VBox gameOverLayout = new VBox(40);
        gameOverLayout.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setFont(pixelFont);
        gameOverLabel.setStyle("-fx-text-fill: white;");
        
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(pixelFont);
        scoreLabel.setStyle("-fx-text-fill: white;");
        
        Button restartButton = new Button("Restart");
        restartButton.setFont(pixelFont);
        restartButton.setOnAction(e -> mainApp.setupGame());
        
        Button quitButton = new Button("Quit");
        quitButton.setFont(pixelFont);
        quitButton.setOnAction(e -> stage.close());
        
        gameOverLayout.getChildren().addAll(gameOverLabel, scoreLabel, restartButton, quitButton);
        gameOverLayout.setStyle("-fx-background-color: black;");
        
        Scene gameOverScene = new Scene(gameOverLayout, WIDTH, HEIGHT);
        stage.setScene(gameOverScene);
    }

    public void drawLaneLines() {
        gc.setStroke(Color.WHITE); // Make lines white for better visibility
        gc.setLineWidth(2); // Make lines thicker
        
        // Draw horizontal lines between lanes
        for (int i = 0; i <= 13; i++) { // Changed to <= to draw the bottom line
            double y = i * LANE_HEIGHT;
            gc.strokeLine(0, y, WIDTH, y);
        }
    }

    public String getPlayerName() {
        return mainApp.getPlayerName();
    }

    private void onGameOver() {
        System.out.println("Tempo scaduto!");
        // Puoi chiamare un metodo per fermare il gioco o visualizzare una schermata di game over.
        renderGameOver(0);  // Per esempio, mostra la schermata di game over
    }

    private void startTimer() {
        // Creiamo una Timeline che aggiorna la barra di progresso ogni secondo
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (timeLeft > 0) {
                timeLeft--;  // Decrementa il tempo
                double progress = timeLeft / GAME_DURATION;  // Calcola la percentuale rimanente
                timerBar.setProgress(progress);  // Aggiorna la barra di progresso
            } else {
                timerBar.setProgress(0);  // Se il tempo è finito, la barra è vuota
                onGameOver();  // Chiama la funzione quando il tempo è scaduto
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);  // Ripete il ciclo
        timeline.play();  // Avvia il timer
    }
}