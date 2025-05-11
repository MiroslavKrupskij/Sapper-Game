package com.example.minesweeper.model;

public class GameModel {
    public static class Cell {
        private boolean bomb;
        private boolean flagged;
        private boolean revealed;
        private int adjacentBombs;

        public boolean isBomb() {
            return bomb;
        }
        public void setBomb(boolean bomb) {
            this.bomb = bomb;
        }

        public boolean isFlagged() {
            return flagged;
        }
        public void setFlagged(boolean flagged) {
            this.flagged = flagged;
        }

        public boolean isRevealed() {
            return revealed;
        }
        public void reveal() {
            this.revealed = true;
        }

        public int getAdjacentBombs() {
            return adjacentBombs;
        }
        public void setAdjacentBombs(int adjacentBombs) {
            this.adjacentBombs = adjacentBombs;
        }
    }

    private final int rows;
    private final int cols;
    private final int bombs;
    private final Cell[][] field;

    public GameModel(int rows, int cols, int bombs) {
        this.rows = rows;
        this.cols = cols;
        this.bombs = bombs;
        this.field = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                field[i][j] = new Cell();
            }
        }
    }

    public Cell[][] getField() {
        return field;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getBombs() {
        return bombs;
    }
}