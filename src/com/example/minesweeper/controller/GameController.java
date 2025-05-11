package com.example.minesweeper.controller;

import com.example.minesweeper.app.MinesweeperApp;
import com.example.minesweeper.model.GameModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;


public class GameController {

    @FXML private Label timerLabel;
    @FXML private Label levelLabel;
    @FXML private Label bombCountLabel;
    @FXML private GridPane grid;

    private String levelName;
    private int flagsPlaced = 0;
    private int openedCells = 0;
    private Timeline timer;
    private int secondsElapsed = 0;
    private boolean gameEnded = false;

    private GameModel model;
    private Button[][] buttons;

    public void initializeGame(int rows, int cols, int bombs, String levelName) {
        this.levelName = levelName;
        this.model = new GameModel(rows, cols, bombs);
        this.buttons = new Button[rows][cols];

        levelLabel.setText(levelName);
        bombCountLabel.setText("Бомб: " + bombs);
        startTimer();
        generateField();
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Час: " + secondsElapsed + " сек.");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void generateField() {
        Random random = new Random();
        grid.getChildren().clear();

        int rows = model.getRows();
        int cols = model.getCols();
        GameModel.Cell[][] cells = model.getField();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button button = new Button();
                button.getStyleClass().add("game-button");
                final int r = i, c = j;
                button.setOnMouseClicked(e -> {
                    if (gameEnded) return;
                    if (e.getButton() == MouseButton.PRIMARY) handleClick(r, c);
                    else if (e.getButton() == MouseButton.SECONDARY) handleFlag(r, c);
                });
                grid.add(button, j, i);
                buttons[i][j] = button;
            }
        }

        int bombs = model.getBombs();
        for (int k = 0; k < bombs; k++) {
            int x, y;
            do {
                x = random.nextInt(rows);
                y = random.nextInt(cols);
            } while (cells[x][y].isBomb());
            cells[x][y].setBomb(true);
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!cells[i][j].isBomb()) {
                    int count = countAdjacentBombs(i, j);
                    cells[i][j].setAdjacentBombs(count);
                }
            }
        }
    }

    private int countAdjacentBombs(int row, int col) {
        int count = 0;
        GameModel.Cell[][] cells = model.getField();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < model.getRows() && newCol >= 0 && newCol < model.getCols()) {
                    if (cells[newRow][newCol].isBomb()) count++;
                }
            }
        }
        return count;
    }

    private void handleClick(int row, int col) {
        GameModel.Cell cell = model.getField()[row][col];
        Button btn = buttons[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.reveal();
        btn.setDisable(true);

        if (cell.isBomb()) {
            btn.getStyleClass().add("bomb-button");
            btn.setText("💣");
            revealAllBombs();
            timer.stop();
            gameEnded = true;
            showEndDialog(false);
        } else {
            int count = cell.getAdjacentBombs();
            if (count > 0) btn.setText(String.valueOf(count));
            openedCells++;

            if (count == 0) openAdjacent(row, col);

            if (openedCells == model.getRows() * model.getCols() - model.getBombs()) {
                timer.stop();
                gameEnded = true;
                showEndDialog(true);
            }
        }
    }

    private void handleFlag(int row, int col) {
        GameModel.Cell cell = model.getField()[row][col];
        Button btn = buttons[row][col];
        if (cell.isRevealed()) return;

        if (cell.isFlagged()) {
            btn.setText("");
            cell.setFlagged(false);
            flagsPlaced--;
        } else {
            btn.setText("🚩");
            cell.setFlagged(true);
            flagsPlaced++;
        }
        bombCountLabel.setText("Бомб: " + (model.getBombs() - flagsPlaced));
    }

    private void openAdjacent(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = row + i;
                int c = col + j;
                if (r >= 0 && r < model.getRows() && c >= 0 && c < model.getCols()) {
                    if (!model.getField()[r][c].isRevealed()) handleClick(r, c);
                }
            }
        }
    }

    private void revealAllBombs() {
        for (int i = 0; i < model.getRows(); i++) {
            for (int j = 0; j < model.getCols(); j++) {
                if (model.getField()[i][j].isBomb()) {
                    Button btn = buttons[i][j];
                    btn.getStyleClass().add("bomb-button");
                    btn.setText("💣");
                }
            }
        }
    }

    private void showEndDialog(boolean win) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dialog.fxml"));
            Parent root = loader.load();
            DialogController controller = loader.getController();
            controller.setResult(win ? "Перемога!" : "Поразка!", secondsElapsed);
            controller.setLevelInfo(model.getRows(), model.getCols(), model.getBombs(), levelName);

            Stage dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MinesweeperApp.primaryStage);

            Scene dialogScene = new Scene(root);
            dialogScene.setFill(Color.TRANSPARENT);
            dialogScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            dialogStage.setScene(dialogScene);

            dialogStage.setResizable(false);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
            MinesweeperApp.primaryStage.setScene(scene);
            MinesweeperApp.primaryStage.setTitle("Сапер");
            MinesweeperApp.primaryStage.setResizable(false);
            MinesweeperApp.primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}