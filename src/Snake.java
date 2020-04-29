
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
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
public class Snake {

    private Direction direction;
    private List<Node> body;
    private int remainingNodesToCreate = 0;

    public Snake(int row, int col, int size) {
        body = new ArrayList<>();
        Node firstNode = new Node(row, col);
        body.add(firstNode);
        for (int i = 1; i < size; i++) {
            Node restOfBody = new Node(row + i, col);
            body.add(restOfBody);
        }

        direction = Direction.UP;
    }

    public List getList() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setRemainingNodesToCreate(int remainingNodesToCreate) {
        this.remainingNodesToCreate += remainingNodesToCreate;
    }

    public void paint(Graphics g, int squareWidth, int squareHeight) {
        body.forEach((node) -> {
            Util.drawSquare(g, node.getRow(), node.getCol(), squareWidth, squareHeight, new Color(0, 51, 255));
        });
    }

    public void move() {
        Node insert = null;
        int row = body.get(0).getRow();
        int col = body.get(0).getCol();
        switch (direction) {
            case UP:
                insert = new Node(row - 1, col);
                break;
            case DOWN:
                insert = new Node(row + 1, col);
                break;
            case RIGHT:
                insert = new Node(row, col + 1);
                break;
            case LEFT:
                insert = new Node(row, col - 1);
                break;
        }
        insertFirstNode(insert);
        if (remainingNodesToCreate == 0) {
            removeLastNode();
        } else {
            remainingNodesToCreate--;
        }
    }

    private void insertFirstNode(Node node) {
        body.add(0, node);
    }

    private void removeLastNode() {
        body.remove(body.size() - 1);
    }

    public boolean contains(int row, int col) {
        return body.stream().anyMatch((node) -> (node.getRow() == row && node.getCol() == col));
    }

}
