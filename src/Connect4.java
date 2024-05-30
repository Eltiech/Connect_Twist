class PlayerOld {
    private String name;

    public PlayerOld(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Cell {
    private PlayerOld owner;

    public Cell() {
        this.owner = null;
    }

    public PlayerOld getOwner() {
        return owner;
    }

    public void setOwner(PlayerOld owner) {
        this.owner = owner;
    }

    public boolean isEmpty() {
        return owner == null;
    }
}

class BoardOld {
    private final int COLSIZE = 6;
    private final int ROWSIZE = 7;
    private Cell[][] cells;

    public BoardOld() {
        cells = new Cell[COLSIZE][ROWSIZE];
        for (int i = 0; i < COLSIZE; i++) {
            for (int j = 0; j < ROWSIZE; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public boolean placeToken(int column, PlayerOld playerOld) {
        for (int i = 0; i < ROWSIZE; i++) {
            if (cells[column][i].isEmpty()) {
                cells[column][i].setOwner(playerOld);
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(PlayerOld playerOld) {
        // Check horizontal lines
        for (int i = 0; i < COLSIZE; i++) {
            for (int j = 0; j < ROWSIZE - 3; j++) {
                if (cells[i][j].getOwner() == playerOld &&
                        cells[i][j + 1].getOwner() == playerOld &&
                        cells[i][j + 2].getOwner() == playerOld &&
                        cells[i][j + 3].getOwner() == playerOld) {
                    return true;
                }
            }
        }

        // Check vertical lines
        for (int i = 0; i < COLSIZE - 3; i++) {
            for (int j = 0; j < ROWSIZE; j++) {
                if (cells[i][j].getOwner() == playerOld &&
                        cells[i + 1][j].getOwner() == playerOld &&
                        cells[i + 2][j].getOwner() == playerOld &&
                        cells[i + 3][j].getOwner() == playerOld) {
                    return true;
                }
            }
        }

        // Check diagonals from bottom-left to top-right
        for (int i = 3; i < COLSIZE; i++) {
            for (int j = 0; j < ROWSIZE - 3; j++) {
                if (cells[i][j].getOwner() == playerOld &&
                        cells[i - 1][j + 1].getOwner() == playerOld &&
                        cells[i - 2][j + 2].getOwner() == playerOld &&
                        cells[i - 3][j + 3].getOwner() == playerOld) {
                    return true;
                }
            }
        }

        // Check diagonals from top-left to bottom-right
        for (int i = 3; i < COLSIZE; i++) {
            for (int j = 3; j < ROWSIZE; j++) {
                if (cells[i][j].getOwner() == playerOld &&
                        cells[i - 1][j - 1].getOwner() == playerOld &&
                        cells[i - 2][j - 2].getOwner() == playerOld &&
                        cells[i - 3][j - 3].getOwner() == playerOld) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < COLSIZE; i++) {
            for (int j = 0; j < ROWSIZE; j++) {
                if (cells[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}

public class Connect4 {
    private BoardOld boardOld;
    private PlayerOld playerOld1;
    private PlayerOld playerOld2;
    private PlayerOld currentPlayerOld;

    public Connect4(String player1Name, String player2Name) {
        this.boardOld = new BoardOld();
        this.playerOld1 = new PlayerOld(player1Name);
        this.playerOld2 = new PlayerOld(player2Name);
        this.currentPlayerOld = playerOld1;
    }

    public boolean placeToken(int column) {
        boolean result = boardOld.placeToken(column, currentPlayerOld);
        // if (result && board.checkWin(currentPlayer)) {
        // System.out.println(currentPlayer.getName() + " wins!");
        // } else {
        // switchPlayer();
        // }
        // switchPlayer();
        return result;
    }

    public boolean isPlayer1Turn() {
        return currentPlayerOld == playerOld1;
    }

    public void switchPlayer() {
        currentPlayerOld = (currentPlayerOld == playerOld1) ? playerOld2 : playerOld1;
    }

    public int isGameOver() {
        int result = -1;
        if (boardOld.checkWin(playerOld1)) {
            result = 1;
        } else if (boardOld.checkWin(playerOld2)) {
            result = 2;
        } else if (boardOld.isDraw()) {
            result = 0;
        }
        return result;
    }

}
