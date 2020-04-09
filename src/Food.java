
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

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

    public Food(Snake snake) {
        position = createRandomNode(snake);
        isSpecial = false;
    }

    public Food(Snake snake, boolean isSpecial) {
        this(snake);
        this.isSpecial = isSpecial;
    }

    public Node createRandomNode(Snake snake) {
        List<Node> body = snake.getList();
        Boolean in = true;
        int row = 0;
        int col = 0;
        while (in) {
            row = (int) (Math.random() * 50);
            col = (int) (Math.random() * 50);

            for (Node node : body) {
                if (row == node.getRow() && col == node.getCol()) {
                    break;
                }

            }
            in = false;
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

    // Create all the methods you need here
}
