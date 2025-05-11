package com.example.minesweeper.controller;

import com.example.minesweeper.app.MinesweeperApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class MenuController {

    @FXML
    public void startEasy(ActionEvent event) {
        launchGame(7, 7, 10, "Легкий");
    }

    @FXML
    public void startMedium(ActionEvent event) {
        launchGame(9, 9, 20, "Середній");
    }

    @FXML
    public void startHard(ActionEvent event) {
        launchGame(11, 11, 30, "Важкий");
    }

    @FXML
    public void startExtreme(ActionEvent event) {
        launchGame(12, 12, 40, "Найважчий");
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
            MinesweeperApp.primaryStage.setTitle("Сапер — " + levelName);
            MinesweeperApp.primaryStage.setResizable(false);
            MinesweeperApp.primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
