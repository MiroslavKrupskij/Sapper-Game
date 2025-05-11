package com.example.minesweeper.controller;

import com.example.minesweeper.app.MinesweeperApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class DialogController {

    @FXML private Label resultText;

    private int rows, cols, bombs;
    private String levelName;

    public void setResult(String message, int timeInSeconds) {
        resultText.setText(message + "\nЧас проходження: " + timeInSeconds + " сек.");
    }

    public void setLevelInfo(int rows, int cols, int bombs, String levelName) {
        this.rows = rows;
        this.cols = cols;
        this.bombs = bombs;
        this.levelName = levelName;
    }

    private void launchGame(int rows, int cols, int bombs, String levelName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.initializeGame(rows, cols, bombs, levelName);
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            MinesweeperApp.primaryStage.setScene(scene);
            MinesweeperApp.primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void restartGame() {
        Stage stage = (Stage) resultText.getScene().getWindow();
        stage.close();
        launchGame(rows, cols, bombs, levelName);
    }

    @FXML
    private void goToNextLevel() {
        Stage stage = (Stage) resultText.getScene().getWindow();
        stage.close();

        switch (levelName.toLowerCase()) {
            case "легкий" -> launchGame(9, 9, 20, "Середній");
            case "середній" -> launchGame(11, 11, 30, "Важкий");
            case "важкий" -> launchGame(12, 12, 40, "Найважчий");
            default -> launchGame(7, 7, 10, "Легкий");
        }
    }

    @FXML
    private void goToMenu() {
        Stage stage = (Stage) resultText.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            MinesweeperApp.primaryStage.setScene(scene);
            MinesweeperApp.primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}