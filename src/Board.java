import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.net.*;

public class Board {
    private int rows;
    private int cols;
    private int numMines;
    private int cellsRemain;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int[][] surroundingMines;
    private GameStatus status;

    public enum GameStatus {
        PLAYING,
        WIN,
        LOSE
    }

    public Board(int rows, int cols, boolean multiplayer) {
        this.rows = rows;
        this.cols = cols;
        initBoard(multiplayer);
        this.status = GameStatus.PLAYING;
    }

    public void initBoard(boolean multiplayer) {
        this.numMines = 0;
        cellsRemain = rows * cols;

        // Reset mines, revealed, flagged, and surroundingMines arrays
        mines = new boolean[rows][cols];
        revealed = new boolean[rows][cols];
        flagged = new boolean[rows][cols];
        surroundingMines = new int[rows][cols];

        loadBoard(multiplayer); // Reload mines
        updateSurroundingMines(); // Update surrounding mines count
    }

    private String fetchBoard() {
        try {
            URL url = new URL("http://localhost:3000/api/board");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            String res = response.toString();
            System.out.println("[Info] Get board " + res);
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1 C,4 B,6 C,3 D,7 H,6 B,3 A,2 E,4 A,7 E";
    }

    private void loadBoard(boolean multiplayer) {
        if (multiplayer) {
            numMines = 0;
            String line = fetchBoard();
            StringTokenizer st = new StringTokenizer(line, ",");
            while (st.hasMoreTokens()) {
                String pair = st.nextToken().strip();
                String[] pos = pair.split(" ");
                int x = Integer.parseInt(pos[0]) - 1;
                int y = pos[1].charAt(0) - 'A';
                this.placeMine(x, y);
            }
        } else {
            numMines = 0;
            File f = new File("./map.txt");
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(f));
                String line = reader.readLine();
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    String pair = st.nextToken().strip();
                    String[] pos = pair.split(" ");
                    int x = Integer.parseInt(pos[0]) - 1;
                    int y = pos[1].charAt(0) - 'A';
                    this.placeMine(x, y);
                }
                
                System.out.println("[Info] read board " + line);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void placeMine(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            mines[row][col] = true;
            numMines++;
        }
        updateSurroundingMines();
    }

    private void updateSurroundingMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                surroundingMines[i][j] = countSurroundingMines(i, j);
            }
        }
    }

    private int countSurroundingMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = row + i;
                int c = col + j;
                if (r >= 0 && r < rows && c >= 0 && c < cols && mines[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void revealCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols && !revealed[row][col]) {
            revealed[row][col] = true;
            cellsRemain--;

            if (surroundingMines[row][col] == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int r = row + i;
                        int c = col + j;
                        if (r >= 0 && r < rows && c >= 0 && c < cols) {
                            if (!isFlagged(r, c))
                                revealCell(r, c);
                        }
                    }
                }
            }
        }
    }

    public int getBonus() {
        int b = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (flagged[i][j] && isMine(i, j))
                    b += 5;
            }
        }
        return b;
    }

    public void toggleFlag(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            flagged[row][col] = !flagged[row][col];
        }
    }

    public boolean isGameOver() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mines[i][j] && revealed[i][j]) {
                    System.out.println("\nLose!");
                    status = GameStatus.LOSE;
                    return true;
                }
            }
        }
        if (cellsRemain == numMines) {
            System.out.println("\nWin!");
            status = GameStatus.WIN;
            return true;
        }
        return false;
    }

    public void printBoard() {
        System.out.print("   ");
        for (int i = 0; i < cols; i++) {
            char c = 'A';
            System.out.printf("%c ", c + i);
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < cols; j++) {
                if (!revealed[i][j]) {
                    System.out.print("* ");
                } else {
                    System.out.print(surroundingMines[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public int getNumMines() {
        return numMines;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flagged[row][col];
    }

    public int getSurroundingMinesCount(int row, int col) {
        return surroundingMines[row][col];
    }

    public GameStatus getStatus() {
        return status;
    }

    public Board clone() {
        Board clonedBoard = new Board(this.rows, this.cols, false);

        // Clone mines, revealed, flagged, and surroundingMines arrays
        for (int i = 0; i < this.rows; i++) {
            System.arraycopy(this.mines[i], 0, clonedBoard.mines[i], 0, this.cols);
            System.arraycopy(this.revealed[i], 0, clonedBoard.revealed[i], 0, this.cols);
            System.arraycopy(this.flagged[i], 0, clonedBoard.flagged[i], 0, this.cols);
            System.arraycopy(this.surroundingMines[i], 0, clonedBoard.surroundingMines[i], 0, this.cols);
        }

        // Clone other attributes
        clonedBoard.numMines = this.numMines;
        clonedBoard.cellsRemain = this.cellsRemain;
        clonedBoard.status = this.status;

        return clonedBoard;
    }
}
