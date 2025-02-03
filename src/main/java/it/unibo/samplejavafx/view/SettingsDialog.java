package it.unibo.samplejavafx.view;

import it.unibo.samplejavafx.core.SoundManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsDialog {

    private Stage dialogStage;

    public SettingsDialog(Stage owner) {
        dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Settings");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: black;");

        Label musicVolumeLabel = new Label("Music Volume");
        musicVolumeLabel.setStyle("-fx-text-fill: white;");
        Slider musicVolumeSlider = new Slider(0, 1, SoundManager.getMusicVolume());
        musicVolumeSlider.setShowTickLabels(true);
        musicVolumeSlider.setShowTickMarks(true);

        Label effectsVolumeLabel = new Label("Effects Volume");
        effectsVolumeLabel.setStyle("-fx-text-fill: white;");
        Slider effectsVolumeSlider = new Slider(0, 1, SoundManager.getEffectsVolume());
        effectsVolumeSlider.setShowTickLabels(true);
        effectsVolumeSlider.setShowTickMarks(true);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            SoundManager.setMusicVolume(musicVolumeSlider.getValue());
            SoundManager.setEffectsVolume(effectsVolumeSlider.getValue());
            dialogStage.close();
        });

        layout.getChildren().addAll(musicVolumeLabel, musicVolumeSlider, effectsVolumeLabel, effectsVolumeSlider, saveButton);
        Scene scene = new Scene(layout, 300, 200);
        dialogStage.setScene(scene);
    }

    public void show() {
        dialogStage.showAndWait();
    }
}