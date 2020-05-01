
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    private List<Node> currentMap, snakeBody;
    private String currentMapName;
    private static final String FIRST_MAP = "SquaresMap.csv", SECOND_MAP = "DivisionMap.csv";

    public Walls(List<Node> snakeBody) {
        this.snakeBody = snakeBody;
        currentMap = new ArrayList<>();
    }

    public List<Node> getList() {
        return currentMap;
    }

    public void mapChosen(int index) {
        switch (index) {
            case 1:
                currentMapName = FIRST_MAP;
                mapByFile(currentMapName);
                break;
            case 2:
                currentMapName = SECOND_MAP;
                mapByFile(currentMapName);
                break;

            default:
                insertRandomNodes();
        }
    }

    private void insertRandomNodes() {
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
                currentMap.add(currentNode);
                count++;
            }
        }
    }

    private void mapByFile(String mapName) {
        try {
            //Corverted to try-with-resources
            try (BufferedReader in = new BufferedReader(new FileReader(mapName))) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] coordenates = line.split(";");
                    int initRow = Integer.parseInt(coordenates[0]);
                    int initCol = Integer.parseInt(coordenates[1]);
                    int endRow = Integer.parseInt(coordenates[2]);
                    int endCol = Integer.parseInt(coordenates[3]);
                    makeNodes(initRow, initCol, endRow, endCol);
                }
            }
        } catch (IOException ex) {
        }
    }

    private void makeNodes(int initRow, int initCol, int endRow, int endCol) {
        for (int row = initRow; row <= endRow; row++) {
            for (int col = initCol; col <= endCol; col++) {
                currentMap.add(new Node(row, col));
            }
        }
    }

    public void paint(Graphics g, int squareWidth, int squareHeight) {
        currentMap.forEach((node) -> {
            Util.drawSquare(g, node.getRow(), node.getCol(), squareWidth, squareHeight, new Color(153, 102, 0));
        });
    }

    public boolean contains(int row, int col) {
        return currentMap.stream().anyMatch((node) -> (node.getRow() == row && node.getCol() == col));
    }
}
