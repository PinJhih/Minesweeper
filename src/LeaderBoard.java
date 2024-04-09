import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

public class LeaderBoard extends JLabel {
    private Controller ctrl;

    public void reload(JFXPanel jfxPanel) {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            webEngine.load("http://localhost:3000/api/ranks");

            jfxPanel.setScene(new Scene(webView));
        });

    }

    public LeaderBoard(Controller c) {
        this.setText("Leaderboard");
        this.setHorizontalAlignment(SwingConstants.CENTER);
        ctrl = c;

        JFXPanel jfxPanel = new JFXPanel();
        reload(jfxPanel);

        this.setLayout(new BorderLayout());
        this.add(jfxPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();

        JButton button = new JButton("Exit");
        button.addActionListener(e -> ctrl.switchPanel("MAIN"));
        btnPanel.add(button);

        JButton button1 = new JButton("Refresh");
        button1.addActionListener(e -> reload(jfxPanel));
        btnPanel.add(button1);

        this.add(btnPanel, BorderLayout.SOUTH);
    }
}
