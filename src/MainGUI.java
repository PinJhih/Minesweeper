import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainGUI extends JPanel {
    private Controller controller;

    public MainGUI(Controller controller) {
        this.controller = controller;
        initialize();
    }

    private void initialize() {

        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("MineSweeper");
        titleLabel.setPreferredSize(new Dimension(90, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.darkGray);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        this.add(titleLabel, gbc);

        JButton startButton = createButton("StartGame", e -> {
            showGameGUI();
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(startButton, gbc);

        JButton rulesButton = createButton("Rules", e -> {
            showRules();
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(rulesButton, gbc);

        JButton editMapButton = createButton("EditMap", e -> {
            editMap();
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        this.add(editMapButton, gbc);

        JButton leaderboardButton = createButton("LeaderBoard", e -> {
            showLeaderboard();
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        this.add(leaderboardButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 載入背景圖片
        ImageIcon backgroundImage = new ImageIcon("./img/background.png");
        // 繪製背景圖片
        g.drawImage(backgroundImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        setCustomButtonStyle(button);
        button.addActionListener(actionListener);
        return button;
    }

    private void setCustomButtonStyle(JButton button) {
        button.setForeground(new Color(200, 200, 200));
        button.setBackground(Color.darkGray);
        Font font = new Font("Serif", Font.PLAIN, 14);
        button.setFont(font);
        Dimension buttonSize = new Dimension(200, 60);
        button.setPreferredSize(buttonSize);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(30, 30, 30));
                button.setBackground(Color.lightGray);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(200, 200, 200));
                button.setBackground(Color.darkGray);
            }
        });
    }

    private void showGameGUI() {
        String[] options = { "Single Player", "Multiplayer" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "Select Game Mode:",
                "Game Mode",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            controller.switchPanel("SGAME");
        } else if (choice == 1) {
            controller.switchPanel("GAME");
        }
    }

    private void showRules() {
        controller.switchPanel("RULE");
    }

    private void showLeaderboard() {
        controller.switchPanel("LEAD");
    }

    private void editMap() {
        MineEditorDialog mineEditorDialog = new MineEditorDialog(controller);
        mineEditorDialog.setVisible(true);
    }
}