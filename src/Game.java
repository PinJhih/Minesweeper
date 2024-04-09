import javax.swing.*;
import java.awt.*;

public class Game implements Controller{
    private JFrame mainFrame;
    private JPanel displayPanel;

    private MainGUI mainGUI;
    private GameGUI gameGUI;
    private RuleGUI ruleGUI;
    private LeaderBoard leadGUI;
    public Game(){
        initialize();
    };

    @Override
    public void switchPanel(String panelName){
        CardLayout cardLayout = (CardLayout) displayPanel.getLayout();
        cardLayout.show(displayPanel, panelName);
        if(panelName.equals("GAME"))
            gameGUI.resetGame(true);
    }

    private void initialize(){
        mainFrame = new JFrame("Minesweeper Game");
        mainFrame.setSize(900, 550);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayPanel = new JPanel();
        displayPanel.setLayout(new CardLayout()); 
        mainGUI = new MainGUI(this);
        displayPanel.add(mainGUI, "MAIN");

        gameGUI = new GameGUI(this);
        displayPanel.add(gameGUI, "GAME");

        ruleGUI = new RuleGUI(this);
        displayPanel.add(ruleGUI, "RULE");


        leadGUI = new LeaderBoard(this);
        displayPanel.add(leadGUI, "LEAD");

        mainFrame.add(displayPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    };
}
