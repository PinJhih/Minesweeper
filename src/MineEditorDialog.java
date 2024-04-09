import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MineEditorDialog extends JDialog {
    private Controller controller;
    private JButton[][] buttons;
    private JLabel remainingMinesLabel;
    private int remainingMines;
    private int rows;
    private int cols;
    private ImageIcon bombIcon;

    public MineEditorDialog(Controller controller) {
        this.controller = controller;
        this.rows = 8; // 預設地圖行數
        this.cols = 8; // 預設地圖列數
        this.remainingMines = rows * cols / 6; // 地雷數量為總格子數的1/6
        initialize();
    }

    private void initialize() {
        this.setTitle("Mine Editor");
        this.setLayout(new BorderLayout());
        bombIcon = resizeImageIcon(new ImageIcon(getClass().getResource("./img/bomb.png")), 40, 40);
        JPanel minePanel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setFocusPainted(false);
                int row = i;
                int col = j;

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleMine(button, row, col);
                    }
                });

                buttons[i][j] = button;
                minePanel.add(button);
            }
        }
        updateRemainingMinesLabel();

        JPanel controlPanel = new JPanel();
        JButton randButton = new JButton("Random");
        randButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                random();
            }
        });
        controlPanel.add(randButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingMines == 0) {
                    uploadMap();
                    dispose();
                }
            }
        });
        controlPanel.add(saveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        controlPanel.add(cancelButton);

        this.add(minePanel, BorderLayout.CENTER);
        this.add(remainingMinesLabel, BorderLayout.NORTH);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void random() {
        remainingMines = rows * cols / 6;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j].setIcon(null);
            }
        }

        Random random = new Random();
        for (int i = 0; i < rows * cols / 6; i++) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);

            if (buttons[x][y].getIcon() != null) {
                i--;
                continue;
            }
            toggleMine(buttons[x][y], x, y);
        }
        remainingMines = 0;
    }

    // 切換地雷狀態
    private void toggleMine(JButton button, int row, int col) {
        if (button.getIcon() == null) {
            if (remainingMines > 0) { // 檢查是否還有剩餘地雷可放置
                button.setIcon(bombIcon);
                remainingMines--;
            } else {
                JOptionPane.showMessageDialog(this, "已達到最大地雷數量限制", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            button.setIcon(null);
            remainingMines++;
        }
        updateRemainingMinesLabel(); // 更新剩餘地雷數標籤
    }

    // 更新剩餘地雷數標籤
    private void updateRemainingMinesLabel() {
        if (remainingMinesLabel == null) {
            remainingMinesLabel = new JLabel("剩餘地雷數： " + remainingMines);
        } else {
            remainingMinesLabel.setText("剩餘地雷數： " + remainingMines);
        }
    }

    // 將地圖上傳
    private void uploadMap() {
        StringBuilder mapData = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (buttons[i][j].getIcon() != null) {
                    // 格式化地雷位置数据，用逗号分隔行号和列号
                    mapData.append((i + 1)).append(" ").append((char) ('A' + j)).append(",");
                }
            }
        }

        // 删除末尾的逗号
        if (mapData.length() > 0) {
            mapData.deleteCharAt(mapData.length() - 1);
        }

        try {
            URL url = new URL("http://localhost:3000/api/board");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            String jsonInputString = "{\"board\": \"" + mapData.toString() + "\"}";

            System.out.println(jsonInputString);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                JOptionPane.showMessageDialog(this, "地圖上傳成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "地圖上傳失敗： " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
}
