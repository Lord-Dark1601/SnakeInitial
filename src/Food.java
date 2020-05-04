
import java.awt.Color;
import java.awt.Graphics;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victoralonso
 */
public class Food {

    private Node position;
    private boolean isSpecial;

    public Food(Snake snake, Walls walls, boolean isSpecial) {
        position = createRandomNode(snake, walls);
        this.isSpecial = isSpecial;

    }

    public Node getPosition() {
        return position;
    }

    public boolean isIsSpecial() {
        return isSpecial;
    }

    private Node createRandomNode(Snake snake, Walls wall) {
        Boolean in = true;
        int row = 0;
        int col = 0;
        while (in) {
            row = (int) (Math.random() * 50);
            col = (int) (Math.random() * 50);
            if (!snake.contains(row, col) && !wall.contains(row, col)) {
                in = false;
            }
        }
        Node food = new Node(row, col);
        return food;
    }

    public void paint(Graphics g, int squareWidth, int squareHeight) {
        if (isSpecial) {
            Util.drawSquare(g, position.getRow(), position.getCol(), squareWidth, squareHeight, new Color(0, 0, 0));
        } else {
            Util.drawSquare(g, position.getRow(), position.getCol(), squareWidth, squareHeight, new Color(255, 51, 51));
        }
    }

    public void delete() {
        position.setCol(-1);
        position.setRow(-1);
    }

}
