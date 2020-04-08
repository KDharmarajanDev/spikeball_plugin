package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SpikeBallGameHandler {

    private static ArrayList<SpikeBallGame> games = new ArrayList<>();

    private static ArrayList<SpikeBallNet> nets = new ArrayList<>();

    public ArrayList<SpikeBallGame> getGames(){
        return games;
    }

    public static void addGame(SpikeBallGame game){
        games.add(game);
    }

    public static void removeGame(SpikeBallGame game){
        games.remove(game);
    }

    public static SpikeBallGame getGamePlayerIsIn(Player player){
        for(SpikeBallGame game : games){
            if(game.ifPlayerIsInGame(player)){
                return game;
            }
        }
        return null;
    }

    public static void addSpikeBallNet(SpikeBallNet spikeBallNet){
        nets.add(spikeBallNet);
    }

    public static void addPlayerToGameFromNet(SpikeBallNet spikeBallNet, Player player){
        for(SpikeBallGame game : games){
            if(game.getSpikeBallNet().equals(spikeBallNet)){
                if(!game.addPlayer(player)){

                }
                break;
            }
        }
    }
}
