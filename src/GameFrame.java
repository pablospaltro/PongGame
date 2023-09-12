import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

public class GameFrame extends JFrame {

    GamePanel panel;

    public GameFrame() {

        try {
            panel = new GamePanel();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        this.setTitle("Pong Game");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(panel);
        panel.requestFocus();
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

    }
}
