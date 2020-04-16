
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
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
                    System.out.println("Direction UP");
                    break;
                case KeyEvent.VK_DOWN:
                    snake.setDirection(Direction.DOWN);
                    System.out.println("Direction Down");
                    break;
                case KeyEvent.VK_RIGHT:
                    snake.setDirection(Direction.RIGHT);
                    System.out.println("Direction Right");
                    break;
                case KeyEvent.VK_LEFT:
                    snake.setDirection(Direction.LEFT);
                    System.out.println("Direction Left");
                    break;
            }
            repaint();
        }
    }

    private int numRows;
    private int numCols;
    private Snake snake;
    private Food food;
    private Food specialFood;
    private Timer snakeTimer;
    private Timer specialFoodTimer;
    private int deltaTime;
    private int foodDeltaTime;
    private Node next;
    private Node[][] playBoard;
    private boolean specialFoodVisible;

    public Board() {

        setFocusable(true);
        initComponents();
        myInit();

        snakeTimer = new Timer(deltaTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                next = createNextNode();
                gameOver();
                //if (snake.canMove(next.getRow(), next.getCol())) {
                if (colideFood()) {
                    if (colideNormalFood()) {
                        snake.setRemainingNodesToCreate(1);
                        food = new Food(snake, false);
                    } else {
                        snake.setRemainingNodesToCreate(4);
                        specialFood.delete();
                    }
                }
                snake.move();
                //}
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

        snakeTimer.start();
        specialFoodTimer.start();
        MyKeyAdapter keyAdepter = new MyKeyAdapter();
        addKeyListener(keyAdepter);
    }

    private void myInit() {
        snake = new Snake(24, 24, 4);
        deltaTime = 200;
        foodDeltaTime = 15000;
        food = new Food(snake, false);
        specialFoodVisible = false;
    }

    public Board(int numRows, int numCols) {
        this();
        this.numCols = numCols;
        this.numRows = numRows;
        playBoard = new Node[numRows][numCols];
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

    public void gameOver() {
        if (colideBorders() || colideBody()) {
            snakeTimer.stop();
            specialFoodTimer.stop();
        }
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
                drawSquare(g2d, row, col, new Color(51, 255, 51));
            }
        }
    }

    private void drawSquare(Graphics2D g, int row, int col, Color color) {
        /*Color colors[] = {new Color(51,255,51), new Color(255,51,51),
           new Color(0,0,0), new Color(0,51,255),};*/
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
