
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victoralonso
 */
public class Board extends javax.swing.JPanel {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    snake.setDirection(Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    snake.setDirection(Direction.DOWN);
                    break;
                case KeyEvent.VK_RIGHT:
                    snake.setDirection(Direction.RIGHT);
                    break;
                case KeyEvent.VK_LEFT:
                    snake.setDirection(Direction.LEFT);
                    break;
                case KeyEvent.VK_P:
                    stopTimers();
                    pauseGame.setVisible(true);
            }
            repaint();
        }
    }

    private int numRows;
    private int numCols;
    private int deltaTime;
    private int foodDeltaTime;
    private int timesLevelUp;
    private boolean specialFoodVisible;
    private String playerName;
    private Snake snake;
    private Food food;
    private Food specialFood;
    private Node next;
    private Node[][] playBoard;
    private Timer snakeTimer;
    private Timer specialFoodTimer;
    private StartGame startGame;
    private PauseGame pauseGame;
    private ScoreBoardIncrementer scoreBoard;
    private static final int VALOR_COMIDA_NORMAL = 1;
    private static final int VALOR_COMIDA_ESPECIAL = 4;
    private static final int VALOR_RESTA_DELAY_DELTATIME = 25;

    public Board() {

        setFocusable(true);
        initComponents();
        myInit();

        snakeTimer = new Timer(deltaTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                next = createNextNode();
                try {
                    gameOver();
                } catch (IOException ex) {
                }
                if (colideFood()) {
                    if (colideNormalFood()) {
                        snake.setRemainingNodesToCreate(VALOR_COMIDA_NORMAL);
                        food = new Food(snake, false);
                        scoreBoard.incrementScore(VALOR_COMIDA_NORMAL);
                    } else {
                        snake.setRemainingNodesToCreate(VALOR_COMIDA_ESPECIAL);
                        specialFood.delete();
                        scoreBoard.incrementScore(VALOR_COMIDA_ESPECIAL);
                    }
                }
                snake.move();
                levelUpVelocity();
                repaint();
            }
        });

        specialFoodTimer = new Timer(foodDeltaTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!specialFoodVisible) {
                    specialFood = new Food(snake, true);
                    int invisibleFoodDeltaTime = (int) (Math.random() * 10000 + 30000);
                    specialFoodTimer.setDelay(invisibleFoodDeltaTime);
                    specialFoodVisible = true;
                } else {
                    specialFood.delete();
                    int visibleFoodDeltaTime = (int) (Math.random() * 5000 + 7000);
                    specialFoodTimer.setDelay(visibleFoodDeltaTime);
                    specialFoodVisible = false;
                }
                repaint();
            }
        });

        //snakeTimer.start();
        //specialFoodTimer.start();
        MyKeyAdapter keyAdepter = new MyKeyAdapter();
        addKeyListener(keyAdepter);
    }

    private void myInit() {
        snake = new Snake(24, 24, 4);
        deltaTime = 200;
        foodDeltaTime = 15000;
        food = new Food(snake, false);
        specialFoodVisible = false;
        timesLevelUp = 1;
    }

    public Board(int numRows, int numCols, ScoreBoardIncrementer scoreBoard, JFrame parent) {
        this();
        this.numCols = numCols;
        this.numRows = numRows;
        playBoard = new Node[numRows][numCols];
        this.scoreBoard = scoreBoard;
        startGame = new StartGame(parent, true, this);
        pauseGame = new PauseGame(parent, true, this);
    }

    public void initGame() {
        resetGame();
        startTimers();
        scoreBoard.setScore(0);
    }

    public void startTimers() {
        snakeTimer.start();
        specialFoodTimer.start();
    }

    private void stopTimers() {
        snakeTimer.stop();
        specialFoodTimer.stop();
    }

    private void resetGame() {
        snake = new Snake(24, 24, 4);
    }

    private void levelUpVelocity() {
        if (scoreBoard.getScore() / VALOR_RESTA_DELAY_DELTATIME == timesLevelUp) {
            deltaTime -= VALOR_RESTA_DELAY_DELTATIME;
            snakeTimer.setDelay(deltaTime);
            timesLevelUp++;
        }
    }

    public void takePlayerName(String playerName) {
        this.playerName = playerName;
    }

    private boolean colideFood() {
        return colideNormalFood() || colideSpecialFood();
    }

    private boolean colideNormalFood() {
        return food.getPosition().getRow() == next.getRow() && food.getPosition().getCol() == next.getCol();
    }

    private boolean colideSpecialFood() {
        if (specialFoodVisible) {
            return specialFood.getPosition().getRow() == next.getRow() && specialFood.getPosition().getCol() == next.getCol();
        }
        return false;
    }

    public void gameOver() throws IOException {
        if (colideBorders() || colideBody()) {
            stopTimers();
            updateScores();
        }
    }

    private void updateScores() throws IOException {
        Player p = new Player(playerName, scoreBoard.getScore());
        startGame.makeList(p);
        startGame.orderList();
        startGame.saveList();
        startGame.printList();
    }

    private boolean colideBorders() {
        return next.getRow() < 0 || next.getRow() > numRows || next.getCol() < 0 || next.getCol() > numCols;
    }

    private boolean colideBody() {
        List<Node> body = snake.getList();
        return body.stream().anyMatch((node) -> (next.getRow() == node.getRow() && next.getCol() == node.getCol()));
    }

    private Node createNextNode() {
        Node reference = (Node) (snake.getList().get(0));
        Node nextNode = new Node(reference.getRow(), reference.getCol());
        switch (snake.getDirection()) {
            case UP:
                nextNode.setRow(nextNode.getRow() - 1);
                break;
            case DOWN:
                nextNode.setRow(nextNode.getRow() + 1);
                break;
            case RIGHT:
                nextNode.setCol(nextNode.getCol() + 1);
                break;
            case LEFT:
                nextNode.setCol(nextNode.getCol() - 1);
                break;
        }

        return nextNode;
    }

    private int squareWidth() {
        return getWidth() / numCols;
    }

    private int squareHeight() {
        return getHeight() / numRows;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //paintPlayBoard(g2d);
        snake.paint(g2d, squareWidth(), squareHeight());
        food.paint(g2d, squareWidth(), squareHeight());
        if (specialFoodVisible) {
            specialFood.paint(g2d, squareWidth(), squareHeight());
        }
    }

    private void paintPlayBoard(Graphics2D g2d) {
        for (int row = 0; row < playBoard.length; row++) {
            for (int col = 0; col < playBoard[0].length; col++) {
                drawSquare(g2d, row, col, Color.pink);
            }
        }
    }

    private void drawSquare(Graphics2D g, int row, int col, Color color) {
        int x = col * squareWidth();
        int y = row * squareHeight();
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
