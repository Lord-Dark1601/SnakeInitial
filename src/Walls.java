
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
 * @author Nebots
 */
public class Walls {

    private List<Node> map0;
    private List<Node> map1;
    private List<Node> map2;
    private List<Node> map3;
    private List<Node> map4;
    private List<Node> map5;
    private List<Node> map6;
    private List<Node> map7;
    private List<Node> map8;
    private List<Node> map9;
    private List[] wallsList;
    private List<Node> snakeBody;

    public Walls(List<Node> snakeBody) {
        wallsList = new List[10];
        this.snakeBody = snakeBody;
        insertRandomNodes();
        wallsList[0] = map0;
    }

    public List[] getList() {
        return wallsList;
    }

    private void insertRandomNodes() {
        map0 = new ArrayList<>();
        int count = 0;
        int nodesCount;
        while (count < 100) {
            nodesCount = 0;
            int x = (int) (Math.random() * 46 + 2);
            int y = (int) (Math.random() * 46 + 2);
            for (Node node : snakeBody) {
                if (node.getRow() == x && node.getCol() == y) {
                    break;
                } else {
                    nodesCount++;
                }
            }
            if (nodesCount == snakeBody.size()) {
                Node currentNode = new Node(x, y);
                map0.add(currentNode);
                count++;
            }
        }
    }

    public void paint(Graphics g, int squareWidth, int squareHeight) {
        map0.forEach((node) -> {
            Util.drawSquare(g, node.getRow(), node.getCol(), squareWidth, squareHeight, new Color(153, 102, 0));
        });
    }
}
