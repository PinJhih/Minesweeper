import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class GameGUI extends JPanel {
    private Controller controller;
    private Board board;
    private JButton[][] buttons;
    private ImageIcon bombIcon;
    private ImageIcon flagIcon;
    private ImageIcon minesIcon;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private JLabel flagsLabel;
    private Timer gameTimer;
    private int secondsPassed;
    private JPanel gamePanel;
    private List<GameSnapshot> gameSnapshots; // 用於存儲遊戲狀態快照
    private int currentSnapshotIndex; // 當前遊戲快照索引
    private boolean replayFinished;
    private int replaySpeed = 1000; // 初始重播速度為1秒
    private Timer replayTimer;
    private JButton speedButton;
    private JPanel labelPanel;
    private int numFlags;

    private static class GameSnapshot {
        private Board board;
        private int secondsPassed;

        public GameSnapshot(Board board, int secondsPassed) {
            this.board = board;
            this.secondsPassed = secondsPassed;
        }

        public Board getBoard() {
            return board;
        }

        public int getSecondsPassed() {
            return secondsPassed;
        }
    }

    public GameGUI(Controller controller) {
        this.controller = controller;
        this.board = new Board(8, 8);
        this.buttons = new JButton[board.getRows()][board.getCols()];
        bombIcon = resizeImageIcon(new ImageIcon(getClass().getResource("./img/bomb.png")), 40, 40);
        flagIcon = resizeImageIcon(new ImageIcon(getClass().getResource("./img/flag.png")), 40, 40);
        minesIcon = resizeImageIcon(new ImageIcon(getClass().getResource("./img/mines.png")), 40, 40);
        this.gameSnapshots = new ArrayList<>();
        this.currentSnapshotIndex = -1; // 初始化為-1，表示沒有快照
        this.replayFinished = false;
        this.secondsPassed = 0;
        initialize();
        startTimer();
    }

    private void initialize() {
        this.setLayout(new GridBagLayout()); // 使用GridBagLayout

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;

        timerLabel = new JLabel("000");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED); // 設置文字顏色為紅色
        timerLabel.setOpaque(true); // 設置為不透明以顯示背景色
        timerLabel.setBackground(Color.BLACK); // 設置背景色為黑色
        timerLabel.setPreferredSize(new Dimension(70, 40));

        scoreLabel = new JLabel("111");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(Color.RED); // 設置文字顏色為紅色
        scoreLabel.setOpaque(true); // 設置為不透明以顯示背景色
        scoreLabel.setBackground(Color.BLACK); // 設置背景色為黑色
        scoreLabel.setPreferredSize(new Dimension(70, 40));

        flagsLabel = new JLabel("111");
        flagsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        flagsLabel.setForeground(Color.RED); // 設置文字顏色為紅色
        flagsLabel.setOpaque(true); // 設置為不透明以顯示背景色
        flagsLabel.setBackground(Color.BLACK); // 設置背景色為黑色
        flagsLabel.setPreferredSize(new Dimension(70, 40));

        // 設置特殊的數位顯示器字體
        try {
            Font digitalFont = Font.createFont(Font.TRUETYPE_FONT, new File("./font/timerFont.ttf")).deriveFont(48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(digitalFont);

            // 使用三個單獨的 JLabel 來顯示時間
            timerLabel = new JLabel();
            timerLabel.setLayout(new GridLayout(1, 3));
            timerLabel.setBackground(Color.BLACK);
            timerLabel.setOpaque(true);

            JLabel digit1 = new JLabel("0");
            digit1.setForeground(Color.RED);
            digit1.setHorizontalAlignment(SwingConstants.RIGHT);
            digit1.setFont(digitalFont);
            timerLabel.add(digit1);

            JLabel digit2 = new JLabel("0");
            digit2.setForeground(Color.RED);
            digit2.setHorizontalAlignment(SwingConstants.RIGHT);
            digit2.setFont(digitalFont);
            timerLabel.add(digit2);

            JLabel digit3 = new JLabel("0");
            digit3.setForeground(Color.RED);
            digit3.setHorizontalAlignment(SwingConstants.RIGHT);
            digit3.setFont(digitalFont);
            timerLabel.add(digit3);

            // 設置特殊的數位顯示器字體
            FontMetrics fm = digit1.getFontMetrics(digitalFont);
            int width = fm.charWidth('0');
            int height = fm.getHeight();
            timerLabel.setPreferredSize(new Dimension(width * 3, height));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        // 設置特殊的數位顯示器字體
        try {
            Font digitalFont = Font.createFont(Font.TRUETYPE_FONT, new File("./font/timerFont.ttf")).deriveFont(48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(digitalFont);

            // 使用三個單獨的 JLabel 來顯示時間
            scoreLabel = new JLabel();
            scoreLabel.setLayout(new GridLayout(1, 3));
            scoreLabel.setBackground(Color.BLACK);
            scoreLabel.setOpaque(true);

            JLabel digit1 = new JLabel("0");
            digit1.setForeground(Color.RED);
            digit1.setHorizontalAlignment(SwingConstants.RIGHT);
            digit1.setFont(digitalFont);
            scoreLabel.add(digit1);

            JLabel digit2 = new JLabel("0");
            digit2.setForeground(Color.RED);
            digit2.setHorizontalAlignment(SwingConstants.RIGHT);
            digit2.setFont(digitalFont);
            scoreLabel.add(digit2);

            JLabel digit3 = new JLabel("0");
            digit3.setForeground(Color.RED);
            digit3.setHorizontalAlignment(SwingConstants.RIGHT);
            digit3.setFont(digitalFont);
            scoreLabel.add(digit3);

            // 設置特殊的數位顯示器字體
            FontMetrics fm = digit1.getFontMetrics(digitalFont);
            int width = fm.charWidth('0');
            int height = fm.getHeight();
            scoreLabel.setPreferredSize(new Dimension(width * 3, height));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        // 設置特殊的數位顯示器字體
        try {
            Font digitalFont = Font.createFont(Font.TRUETYPE_FONT, new File("./font/timerFont.ttf")).deriveFont(48f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(digitalFont);

            // 使用三個單獨的 JLabel 來顯示時間
            flagsLabel = new JLabel();
            flagsLabel.setLayout(new GridLayout(1, 3));
            flagsLabel.setBackground(Color.BLACK);
            flagsLabel.setOpaque(true);

            JLabel digit1 = new JLabel("0");
            digit1.setForeground(Color.RED);
            digit1.setHorizontalAlignment(SwingConstants.RIGHT);
            digit1.setFont(digitalFont);
            flagsLabel.add(digit1);

            JLabel digit2 = new JLabel("0");
            digit2.setForeground(Color.RED);
            digit2.setHorizontalAlignment(SwingConstants.RIGHT);
            digit2.setFont(digitalFont);
            flagsLabel.add(digit2);

            JLabel digit3 = new JLabel("0");
            digit3.setForeground(Color.RED);
            digit3.setHorizontalAlignment(SwingConstants.RIGHT);
            digit3.setFont(digitalFont);
            flagsLabel.add(digit3);

            // 設置特殊的數位顯示器字體
            FontMetrics fm = digit1.getFontMetrics(digitalFont);
            int width = fm.charWidth('0');
            int height = fm.getHeight();
            flagsLabel.setPreferredSize(new Dimension(width * 3, height));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        labelPanel = new JPanel(new GridLayout(2, 3, 25, 0));
        labelPanel.setOpaque(true);
        labelPanel.setBackground(Color.BLACK);

        JLabel timeLabel_ = new JLabel("Time");
        timeLabel_.setHorizontalAlignment(SwingConstants.CENTER); // 將文字置中
        timeLabel_.setForeground(Color.WHITE); // 設置文字顏色為白色
        timeLabel_.setFont(timeLabel_.getFont().deriveFont(Font.BOLD, 20));
        labelPanel.add(timeLabel_);

        JLabel scoreLabel_ = new JLabel("Score");
        scoreLabel_.setHorizontalAlignment(SwingConstants.CENTER); // 將文字置中
        scoreLabel_.setForeground(Color.WHITE); // 設置文字顏色為白色
        scoreLabel_.setFont(scoreLabel_.getFont().deriveFont(Font.BOLD, 20));
        labelPanel.add(scoreLabel_);

        JLabel minesLabel_ = new JLabel("#Flags");
        minesLabel_.setHorizontalAlignment(SwingConstants.CENTER); // 將文字置中
        minesLabel_.setForeground(Color.WHITE); // 設置文字顏色為白色
        minesLabel_.setFont(minesLabel_.getFont().deriveFont(Font.BOLD, 20));
        labelPanel.add(minesLabel_);

        labelPanel.add(timerLabel);
        labelPanel.add(scoreLabel);
        labelPanel.add(flagsLabel);

        this.add(labelPanel, gbc);
        speedButton = new JButton("Speed: 1x");
        speedButton.setVisible(false);
        speedButton.addActionListener(e -> {
            if (replaySpeed == 1000) {
                replaySpeed = 750;
                speedButton.setText("Speed: 1.5x");
            } else if (replaySpeed == 750) {
                replaySpeed = 500;
                speedButton.setText("Speed: 2x");
            } else {
                replaySpeed = 1000;
                speedButton.setText("Speed: 1x");
            }
            if (replayTimer != null && replayTimer.isRunning()) {
                replayTimer.setDelay(replaySpeed); // 更新重播速度
            }
        });
        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(speedButton, gbc); // 添加按鈕到主面板
        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gamePanel = new JPanel(); // 初始化GamePanel
        gamePanel.setLayout(new GridLayout(board.getRows(), board.getCols()));
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setFocusPainted(false);
                int row = i;
                int col = j;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleButtonClick(row, col);
                    }
                });

                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        if (SwingUtilities.isRightMouseButton(evt)) {
                            handleRightClick(row, col);
                        }
                    }
                });

                buttons[i][j] = button;
                gamePanel.add(button); // 將button添加到GamePanel
            }
        }
        this.add(gamePanel, gbc); // 將GamePanel添加到主面板

        // 添加返回主選單的按鈕
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14)); // 設定字體和大小
        backButton.setForeground(new Color(200, 200, 200)); // 設定文字顏色
        backButton.setBackground(Color.darkGray); // 設定按鈕背景顏色
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // 設定邊框
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setForeground(new Color(30, 30, 30));
                backButton.setBackground(Color.lightGray);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setForeground(new Color(200, 200, 200));
                backButton.setBackground(Color.darkGray);
            }
        });
        backButton.addActionListener(e -> {
            controller.switchPanel("MAIN");
        });
        gbc.gridy = 3;
        gbc.weighty = 0.2;
        this.add(backButton, gbc);
    }

    public void resetGame(boolean clear) {
        // 停止計時器並重置秒數
        gameTimer.stop();

        // 重置按鈕狀態和圖標
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                JButton button = buttons[i][j];
                button.setIcon(null);
                button.setText("");
                button.setEnabled(true);
            }
        }
        labelPanel.setVisible(true);
        speedButton.setVisible(false);
        if (clear) {
            gameSnapshots.clear();
            replayFinished = false;
        }
        secondsPassed = 0;
        updateLabel(timerLabel, "000");

        startTimer();
        // 重置棋盤狀態
        board.initBoard();
        
        numFlags = board.getNumMines();
        updateLabel(flagsLabel, String.format("%03d", numFlags));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 載入背景圖片
        ImageIcon backgroundImage = new ImageIcon("./img/background.png");
        // 繪製背景圖片
        g.drawImage(backgroundImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
    }

    private void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsPassed++;
                String timeString = String.format("%03d", secondsPassed);
                updateLabel(timerLabel, timeString); // 更新 timerLabel 的文字
            }
        });
        gameTimer.start();
    }

    private void updateLabel(JLabel label, String timeString) {
        String digit1 = timeString.substring(0, 1);
        String digit2 = timeString.substring(1, 2);
        String digit3 = timeString.substring(2, 3);

        ((JLabel) label.getComponent(0)).setText(digit1);
        ((JLabel) label.getComponent(1)).setText(digit2);
        ((JLabel) label.getComponent(2)).setText(digit3);
    }

    private void showNameInputDialog() {
        String name = "";
        int score = secondsPassed;
        while (name.trim().isEmpty()) {
            JTextField textField = new JTextField();
            Object[] message = {
                    "Enter your name:", textField
            };
            int option = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Name Input",
                    JOptionPane.OK_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[] { "OK" },
                    "OK");

            if (option == JOptionPane.OK_OPTION) {
                name = textField.getText();

                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {

                    JOptionPane.showMessageDialog(this, "Name: " + name + " Score:" + score, "Score",
                            JOptionPane.PLAIN_MESSAGE);
                    uploadScore(name, score); // 上傳成績
                }
            } else {
                JOptionPane.showMessageDialog(this, "Cancel upload score!", "Warn", JOptionPane.WARNING_MESSAGE);
                JOptionPane.showMessageDialog(this, " Score:" + score, "Score",
                        JOptionPane.PLAIN_MESSAGE);
                break;
            }
        }
    }

    private void uploadScore(String name, int score) {
        try {
            String urlStr = String.format("http://localhost:3000/api/records/%s/%d", name, score);
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int resCode = connection.getResponseCode();
            System.out.println("[Info] Upload score (" + resCode + ")");
            connection.disconnect();
        } catch (Exception e) {
            System.err.println("[Error] Cannot upload score\n" + e);
        }
    }

    private void showReplayDialog(String gameResult) {
        int choice = JOptionPane.showOptionDialog(
                this,
                gameResult + " Do you want to replay?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[] { "Yes", "No" },
                "Yes");

        if (choice == JOptionPane.YES_OPTION) {
            replayGame();
        } else {
            replayFinished = true;
        }
    }

    private void handleButtonClick(int row, int col) {
        if (!board.isRevealed(row, col)) {
            board.revealCell(row, col);
            updateButtons();
            addGameSnapshot();
            if (board.isGameOver()) {
                handleGameOver();
            }
        }
    }

    private void handleGameOver() {
        gameTimer.stop();
        String gameResult = "";
        if (board.getStatus() == Board.GameStatus.WIN) {
            gameResult = "Win!";
        } else if (board.getStatus() == Board.GameStatus.LOSE) {
            gameResult = "Failure!";
            this.gameFailed();
        }
        showReplayDialog(gameResult);

        // 使用計時器來檢查showReplayDialog是否已經關閉
        Timer postReplayTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (replayFinished) { // 假設isReplayDialogShowing是一個方法，用於檢查showReplayDialog是否正在顯示
                    // 停止postReplayTimer
                    ((Timer) e.getSource()).stop();
                    showNameInputDialog(); // 彈出對話框讓玩家輸入名稱
                    controller.switchPanel("MAIN");
                }
            }
        });

        postReplayTimer.start();
    }

    private void gameFailed() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                if (board.isMine(i, j)) {
                    JButton button = buttons[i][j];
                    button.setIcon(minesIcon);
                }
            }
        }
    }

    private void handleRightClick(int row, int col) {
        if (!board.isRevealed(row, col)) {
            if (board.isFlagged(row, col)) {
                numFlags++;
            } else {
                numFlags--;
            }
            board.toggleFlag(row, col);
            updateButtons();
            addGameSnapshot();
            updateLabel(flagsLabel, String.format("%03d", numFlags));
        }
    }

    private void addGameSnapshot() {
        GameSnapshot snapshot = new GameSnapshot(board.clone(), secondsPassed);
        gameSnapshots.add(snapshot);
        currentSnapshotIndex++;
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void updateButtons() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                JButton button = buttons[i][j];
                if (board.isRevealed(i, j)) {
                    if (board.isMine(i, j)) {
                        button.setIcon(bombIcon);
                    } else {
                        int surroundingMines = board.getSurroundingMinesCount(i, j);
                        if (surroundingMines > 0) {
                            button.setText(String.valueOf(surroundingMines));
                        } else {
                            button.setText("");
                        }
                    }
                    button.setEnabled(false);
                } else {
                    if (board.isFlagged(i, j)) {
                        button.setIcon(flagIcon);
                    } else {
                        button.setIcon(null);
                    }
                    button.setEnabled(true);
                }
            }
        }
    }

    private void replayGame() {
        if (gameSnapshots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No game snapshots available for replay!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        resetGame(false);
        gameTimer.stop();
        currentSnapshotIndex = 0;
        labelPanel.setVisible(false);
        speedButton.setVisible(true);

        Timer replayTimer = new Timer(250, new ActionListener() {
            int t = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                t += 250;

                if (t % replaySpeed == 0) {
                    if (currentSnapshotIndex < gameSnapshots.size()) {
                        GameSnapshot snapshot = gameSnapshots.get(currentSnapshotIndex);
                        board = snapshot.getBoard().clone(); // 使用克隆的Board對象
                        secondsPassed = snapshot.getSecondsPassed();
                        updateButtons();
                        currentSnapshotIndex++;

                        if (currentSnapshotIndex == gameSnapshots.size()) {
                            gameFailed();
                        }
                    } else {
                        replayFinished = true;
                        gameTimer.stop();
                        ((Timer) e.getSource()).stop();
                    }
                }
            }
        });
        replayTimer.start();
    }

}