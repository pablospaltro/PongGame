import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class Score extends Rectangle{

    static int GAME_WIDTH;
    static int GAME_HEIGTH;
    int player1;
    int player2;

    public Score(int GAME_WIDTH, int GAME_HEIGTH) {
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGTH = GAME_HEIGTH;
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.white);
        g.setFont(new Font("Consolas", Font.PLAIN, 60));

        //g.drawLine(GAME_WIDTH/2, 0, GAME_WIDTH/2, GAME_HEIGTH);

        Stroke dashed = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{14}, 30);
        g2d.setStroke(dashed);
        g2d.drawLine(GAME_WIDTH/2, 0, GAME_WIDTH/2, GAME_HEIGTH);

        g.drawString(String.valueOf(player1), (GAME_WIDTH/2)-115, 50);
        g.drawString(String.valueOf(player2), (GAME_WIDTH/2)+95, 50);


    }




}
