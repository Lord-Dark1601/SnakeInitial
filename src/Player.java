/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;

/**
 *
 * @author 10214291
 */
public class Player implements Serializable, Comparable<Player> {

    private String name;
    private int score;

    public Player() {
        name = "";
        score = 0;
    }

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    public int getScore(){
        return score;
    }

    @Override
    public int compareTo(Player player) {
        return player.score - score ;
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }
}
