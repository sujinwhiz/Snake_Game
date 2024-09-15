import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    final static int BOARD_WIDTH = 600;
    final static int BOARD_HEIGHT = 600;
    final static int TILE_SIZE = 20;
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake_Game");
        SnakeGame snakeGame = new SnakeGame(BOARD_WIDTH, BOARD_HEIGHT, TILE_SIZE);
        frame.add(snakeGame);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        snakeGame.requestFocus();
    }
}

class SnakeGame extends JPanel implements ActionListener, KeyListener{

    class Tile {
        int xPos, yPos;
        public Tile(int xPos, int yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }
    }

    final int BOARD_WIDTH;
    final int BOARD_HEIGHT;
    final int TILE_SIZE;
    Tile snakeHead;
    Tile snakeFood;
    Timer gameLoop;
    ArrayList<Tile> snakeBody;
    Random random;
    int xVel = 1, yVel = 0, iniSpeed = 1;
    int score = 0, highScore = 0;

    public SnakeGame(int BOARD_WIDTH, int BOARD_HEIGHT,int TILE_SIZE) {
        this.BOARD_WIDTH = BOARD_WIDTH;
        this.BOARD_HEIGHT = BOARD_HEIGHT;
        this.TILE_SIZE = TILE_SIZE;
        setPreferredSize(new Dimension(this.BOARD_WIDTH, this.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        snakeHead = new Tile(15,15);
        snakeFood = new Tile(5,5);
        snakeBody = new ArrayList<>();
        random = new Random();
        gameLoop = new Timer(200,this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillRoundRect( snakeFood.xPos * TILE_SIZE, snakeFood.yPos * TILE_SIZE,TILE_SIZE - 5,TILE_SIZE -5,20,20);
        g.setColor(Color.decode("#006400"));
        g.fill3DRect(snakeHead.xPos * TILE_SIZE, snakeHead.yPos * TILE_SIZE,TILE_SIZE,TILE_SIZE,true);
        g.setColor(Color.green);
        for(Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.xPos * TILE_SIZE, snakePart.yPos * TILE_SIZE,TILE_SIZE,TILE_SIZE,true);
        }
        score = snakeBody.size();
        if (score > highScore) {
            highScore = score;
        }
        g.setColor(Color.darkGray);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("SCORE : " + score, BOARD_WIDTH - 580, 55);
        g.drawString("HIGH SCORE  : " + highScore, BOARD_WIDTH - 140, 55);
    }

    public void move() {
        if(collision(snakeFood,snakeHead)) {
            snakeBody.add(new Tile(snakeFood.xPos,snakeFood.yPos));
            placeFood();
        }
        for(int i = snakeBody.size() -1; i >= 0; --i) {
            Tile snakePart = snakeBody.get(i);
            if(i == 0) {
                snakePart.xPos = snakeHead.xPos;
                snakePart.yPos = snakeHead.yPos;
            }
            else {
                Tile snakePrevPart = snakeBody.get(i - 1);
                snakePart.xPos = snakePrevPart.xPos;
                snakePart.yPos = snakePrevPart.yPos;
            }
        }
        snakeHead.xPos += xVel;
        snakeHead.yPos += yVel;
    }

    public void placeFood() {
        boolean validPosition = false;
        while(!validPosition) {
            snakeFood.xPos = random.nextInt(BOARD_WIDTH / TILE_SIZE);
            snakeFood.yPos = random.nextInt(BOARD_HEIGHT / TILE_SIZE);
            validPosition = true;
            for(Tile snakePart : snakeBody) {
                if(collision(snakeFood,snakePart)) {
                    validPosition = false;
                    break;
                }
            }
        }
        if (score % 5 == 0 && gameLoop.getDelay() > 50 && score != 0) {
            gameLoop.setDelay(gameLoop.getDelay() - 10);
            System.out.println(gameLoop.getDelay());
        }
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.xPos == t2.xPos && t1.yPos == t2.yPos;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver()) {
            gameLoop.stop();
            int option = JOptionPane.showOptionDialog(this,
                     "GAME_OVER :( \n \n FINAL SCORE : "+score+"\n PLAY AGAIN ?","GAME_OVER",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                     null,null,null);
            switch(option) {
                case 0 :
                    snakeHead = new Tile(15,15);
                    snakeFood = new Tile(5,5);
                    snakeBody = new ArrayList<>();
                    gameLoop.setDelay(200);
                    gameLoop.start();
                    break;
                case 1 :
                    System.exit(0);
                    break;
            }
        }
    }

    private boolean gameOver() {
         for(Tile snakePart : snakeBody) {
             if(collision(snakePart,snakeHead)) return true;
         }

        return snakeHead.xPos < 0 || snakeHead.xPos >= (BOARD_WIDTH / TILE_SIZE) ||
                snakeHead.yPos < 0 || snakeHead.yPos >= (BOARD_HEIGHT / TILE_SIZE);

    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && yVel != iniSpeed) {
             xVel = 0;
             yVel = -iniSpeed;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && yVel != -iniSpeed) {
            xVel = 0;
            yVel = iniSpeed;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && xVel != iniSpeed) {
            xVel = -iniSpeed;
            yVel = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && xVel != -iniSpeed) {
            xVel = iniSpeed;
            yVel = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}


}