import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 800;
    static final int GAME_HEIGTH = (int) (GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGTH);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 15;
    static final int PADDLE_HEIGTH = 70;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    Clip paddleHitSound;
    Clip wallHitSound;
    Clip scoreSound;
    String winMessage = "WIN";
    Font largeFont = new Font("Consolas", Font.PLAIN, 90);
    boolean running = true;
    boolean paused = false;


    public GamePanel() throws LineUnavailableException {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGTH);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();

        paddleHitSound = AudioSystem.getClip();
        wallHitSound = AudioSystem.getClip();
        scoreSound = AudioSystem.getClip();

        loadSound("PongSoundPaddle.wav", paddleHitSound);
        loadSound("PongSoundWall.wav", wallHitSound);
        loadSound("PongSoundScore.wav", scoreSound);
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGTH - BALL_DIAMETER), BALL_DIAMETER, BALL_DIAMETER);


    }

    public void newPaddles() {
        int paddle1X = 20;
        int paddle2X = GAME_WIDTH - PADDLE_WIDTH - 20;
        paddle1 = new Paddle(paddle1X, (GAME_HEIGTH / 2) - (PADDLE_HEIGTH / 2), PADDLE_WIDTH, PADDLE_HEIGTH, 1);
        paddle2 = new Paddle(paddle2X, (GAME_HEIGTH / 2) - (PADDLE_HEIGTH / 2), PADDLE_WIDTH, PADDLE_HEIGTH, 2);
    }


   /*
    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);

    }
    */

    public void resetGame() {
        score.player1 = 0;
        score.player2 = 0;
        newPaddles();
        newBall();
        paused = false;
        running=true;
    }
    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();

        drawWhilePause(graphics);

        if (score.player1 == 10) {
            graphics.setColor(Color.blue);
            graphics.setFont(largeFont);
            int winMessageX = (GAME_WIDTH / 2) - 300;
            int winMessageY = GAME_HEIGTH / 2;
            graphics.drawString(winMessage, winMessageX, winMessageY);

            String playAgainMessage = "PLAY AGAIN (spacebar)";
            graphics.setFont(new Font("Consolas", Font.PLAIN, 20));
            int playAgainX = (GAME_WIDTH / 2) - 300;
            int playAgainY = winMessageY + 50;
            graphics.drawString(playAgainMessage, playAgainX, playAgainY);
        } else if(score.player2 == 10) {
            graphics.setColor(Color.red);
            graphics.setFont(largeFont);
            int winMessageX = (GAME_WIDTH / 2) + 80;
            int winMessageY = GAME_HEIGTH / 2;
            graphics.drawString(winMessage, winMessageX, winMessageY);

            String playAgainMessage = "PLAY AGAIN (spacebar)";
            graphics.setFont(new Font("Consolas", Font.PLAIN, 20));
            int playAgainX = (GAME_WIDTH / 2) + 80;
            int playAgainY = winMessageY + 50;
            graphics.drawString(playAgainMessage, playAgainX, playAgainY);


        }
        else {
            draw(graphics);
        }

        g.drawImage(image, 0, 0, this);
    }





    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);

    }

    public void drawWhilePause(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        score.draw(g);
    }



    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
    }

    public void checkCollision() {
        //bounce ball off top & bottom  window edges
        if (ball.y <= 0) {
            ball.setYDirection(-ball.yVelocity);
            addWallSound();
        }
        if (ball.y >= GAME_HEIGTH - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
            addWallSound();
        }

        //bounce ball off paddles
        if (ball.intersects(paddle1)) {
            addPaddleSound();
            ball.xVelocity = Math.abs(ball.xVelocity);
            /*
            ball.xVelocity++; //optional for more difficulty
            if (ball.yVelocity > 0)
                ball.yVelocity++; //optional for more difficulty
            else
                ball.yVelocity--;

             */
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if (ball.intersects(paddle2)) {
            addPaddleSound();
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; //optional for more difficulty
            if (ball.yVelocity > 0)
                ball.yVelocity++; //optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }


        //stops paddles at window edges
        if (paddle1.y < 0) {
            paddle1.y = 0;

        }
        if (paddle1.y >= (GAME_HEIGTH - PADDLE_HEIGTH)) {
            paddle1.y = GAME_HEIGTH - PADDLE_HEIGTH;

        }
        if (paddle2.y < 0) {
            paddle2.y = 0;

        }
        if (paddle2.y >= (GAME_HEIGTH - PADDLE_HEIGTH)) {
            paddle2.y = GAME_HEIGTH - PADDLE_HEIGTH;

        }

        //give a player 1 point and creates new paddles & ball
        if (ball.x <= 0) {
            score.player2++;
            addScoreSound();
            newPaddles();
            newBall();
            System.out.println("Player 2:" + score.player2);
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            addScoreSound();
            newPaddles();
            newBall();
            System.out.println("Player 1:" + score.player1);
        }
    }

    public void addPaddleSound() {
        if (ball.intersects(paddle1)) {
            paddleHitSound.stop();
            paddleHitSound.setFramePosition(0);
            paddleHitSound.start();
        }
        if (ball.intersects(paddle2)) {
            paddleHitSound.stop();
            paddleHitSound.setFramePosition(0);
            paddleHitSound.start();
        }
    }

    public void addWallSound() {
        wallHitSound.stop();
        wallHitSound.setFramePosition(0);
        wallHitSound.start();

    }

    public void addScoreSound() {
        if (ball.x <= 0 || ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            scoreSound.stop();
            scoreSound.setFramePosition(0);
            scoreSound.start();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                if (!paused && isVisible()) {
                    move();
                    checkCollision();
                    repaint();
                }
                delta--;
            }

            // Comprueba si se alcanza una puntuación de 10
            if ((score.player1 == 10 || score.player2 == 10) && !paused) {
                System.out.println("juego terminado");
                paused = true; // Pausa el juego
                repaint(); // Vuelve a pintar para mostrar el mensaje "WIN" y "Press Spacebar to play again"
            }
        }
    }







    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                if (score.player1 == 10 || score.player2 == 10) {
                    resetGame();
                    paused=false;
                    repaint();
                } else {
                    paused = !paused; // Alternar entre pausa y no pausa
                }
            } else {
                paddle1.keyPressed(e);
                paddle2.keyPressed(e);
            }
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
            paddle2.keyReleased(e);
        }
    }



    private void loadSound(String filename, Clip clip) {
        try {
            URL soundURL = getClass().getResource("/resources/" + filename);
            assert soundURL != null;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            clip.open(audioInputStream);


            if (filename.equals("PongSoundPaddle.wav")) {
                paddleHitSound = clip;
            } else if (filename.equals("PongSoundWall.wav")) {
                wallHitSound = clip;
            } else if (filename.equals("PongSoundScore.wav")) {
                scoreSound = clip;
            }

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

}
