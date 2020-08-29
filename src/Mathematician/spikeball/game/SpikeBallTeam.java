package Mathematician.spikeball.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SpikeBallTeam {

    private String name;
    private String color;
    private ArrayList<Player> players;

    public SpikeBallTeam(String name, String color){
        this.name = name;
        this.color = color;
        players = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public String getColor(){
        return color;
    }

    public void addPlayer(Player player){
        if(players.size() < 2){
            players.add(player);
        }
    }

}
