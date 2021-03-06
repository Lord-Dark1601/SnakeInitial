
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
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
 * @Ft. @authors VictorLafuente & Vicent Serra
 */
public class Board extends javax.swing.JPanel {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (snake.getDirection() != Direction.DOWN && puesNoLoSeLaVerdadDirections) {
                        snake.setDirection(Direction.UP);
                        puesNoLoSeLaVerdadDirections = false;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (snake.getDirection() != Direction.UP && puesNoLoSeLaVerdadDirections) {
                        snake.setDirection(Direction.DOWN);
                        puesNoLoSeLaVerdadDirections = false;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (snake.getDirection() != Direction.LEFT && puesNoLoSeLaVerdadDirections) {
                        snake.setDirection(Direction.RIGHT);
                        puesNoLoSeLaVerdadDirections = false;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (snake.getDirection() != Direction.RIGHT && puesNoLoSeLaVerdadDirections) {
                        snake.setDirection(Direction.LEFT);
                        puesNoLoSeLaVerdadDirections = false;
                    }
                    break;
                case KeyEvent.VK_P:
                    stopTimers();
                    pauseGame.setVisible(true);
            }
            repaint();
        }
    }

    private int numRows, numCols, deltaTime, foodDeltaTime, timesLevelUp, levelSelect;
    private boolean paintWalls, specialFoodVisible, mapCreated, puesNoLoSeLaVerdadDirections;
    private String playerName;
    private Snake snake;
    private Food food, specialFood;
    private Node next;
    private Walls walls;
    private Timer snakeTimer, specialFoodTimer;
    private StartGame startGame;
    private PauseGame pauseGame;
    private ScoreBoardIncrementer scoreBoard;
    private static final int VALOR_COMIDA_NORMAL = 1, VALOR_COMIDA_ESPECIAL = 4, VALOR_RESTA_DELAY_DELTATIME = 25;

    public Board() {

        setFocusable(true);
        initComponents();
        myInit();

        //Make lambda expresion??
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
                        food = new Food(snake, walls, false);
                        scoreBoard.incrementScore(VALOR_COMIDA_NORMAL);
                    } else {
                        snake.setRemainingNodesToCreate(VALOR_COMIDA_ESPECIAL);
                        specialFood.delete();
                        scoreBoard.incrementScore(VALOR_COMIDA_ESPECIAL);
                    }
                }
                snake.move();
                levelUpVelocity();
                updateMap();
                puesNoLoSeLaVerdadDirections = true;
                repaint();
            }
        });

        //Make lambda expresion??
        specialFoodTimer = new Timer(foodDeltaTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!specialFoodVisible) {
                    specialFood = new Food(snake, walls, true);
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

        MyKeyAdapter keyAdepter = new MyKeyAdapter();
        addKeyListener(keyAdepter);
    }

    private void myInit() {
        snake = new Snake(24, 24, 4);
        walls = new Walls(snake.getList());
        food = new Food(snake, walls, false);
        deltaTime = 200;
        foodDeltaTime = 15000;
        timesLevelUp = 1;
        paintWalls = false;
        mapCreated = false;
        specialFoodVisible = false;
        puesNoLoSeLaVerdadDirections = true;
    }

    public Board(int numRows, int numCols, ScoreBoardIncrementer scoreBoard, JFrame parent) {
        this();
        this.numCols = numCols;
        this.numRows = numRows;
        this.scoreBoard = scoreBoard;
        startGame = new StartGame(parent, true, this);
        pauseGame = new PauseGame(parent, true, this);
    }

    public void initGame() {
        myInit();
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

    private void levelUpVelocity() {
        if (scoreBoard.getScore() / VALOR_RESTA_DELAY_DELTATIME == timesLevelUp) {
            deltaTime -= VALOR_RESTA_DELAY_DELTATIME;
            snakeTimer.setDelay(deltaTime);
            timesLevelUp++;
        }
    }

    private void updateMap() {
        if (scoreBoard.getScore() > 5) {
            paintWalls = true;
            if (!mapCreated) {
                creatingMap();
            }
        }
    }

    private void creatingMap() {
        walls = new Walls(snake.getList());
        walls.mapChosen(levelSelect);
        mapCreated = true;
    }

    public void takeStartGameFields(String playerName, int levelSelect) {
        this.playerName = playerName;
        this.levelSelect = levelSelect;
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
        if (colideBorders() || colideBody() || colideWalls()) {
            stopTimers();
            updateScores();
        }
    }

    private void updateScores() throws IOException {
        Player p = new Player(playerName, scoreBoard.getScore());
        startGame.startGameCalls(p);
    }

    private boolean colideBorders() {
        return next.getRow() < 0 || next.getRow() > numRows || next.getCol() < 0 || next.getCol() > numCols;
    }

    private boolean colideBody() {
        List<Node> body = snake.getList();
        return body.stream().anyMatch((node) -> (next.getRow() == node.getRow() && next.getCol() == node.getCol()));
    }

    private boolean colideWalls() {
        List<Node> wallsList = walls.getList();
        if (paintWalls) {
            if (wallsList.stream().anyMatch((node) -> (next.getRow() == node.getRow() && next.getCol() == node.getCol()))) {
                return true;
            }
        }
        return false;
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
        snake.paint(g2d, squareWidth(), squareHeight());
        food.paint(g2d, squareWidth(), squareHeight());
        if (specialFoodVisible) {
            specialFood.paint(g2d, squareWidth(), squareHeight());
        }
        if (paintWalls) {
            walls.paint(g2d, squareWidth(), squareHeight());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

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
