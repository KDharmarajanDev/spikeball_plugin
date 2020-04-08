package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class SpikeBallGame {

    private SpikeBall spikeBall;
    private SpikeBallNet spikeBallNet;

    private ArrayList<Player> redTeam;
    private ArrayList<Player> blueTeam;

    private enum GameStates {
        PRACTICE, PRE_MATCH, MATCH
    }

    private GameStates currentState = GameStates.PRE_MATCH;

    public SpikeBallGame(Block spikeBallNet, ArrayList<Player> players){
        redTeam = new ArrayList<>();
        blueTeam = new ArrayList<>();
        if(!(players.size() < 1)){
            for(Player p : players){
                addPlayer(p);
            }
            spikeBall = new SpikeBall(players.get(0).getWorld());
            this.spikeBallNet = new SpikeBallNet(spikeBallNet);
        }
    }

    public void startGame(){
        currentState = GameStates.MATCH;
        Random random = new Random();
        int randomInt = random.nextInt(redTeam.size());
        if(redTeam.size() >= 1){
            spikeBall.giveToPlayer(redTeam.get(randomInt));
        }
    }

    public void serveSpikeBall(Player player, Vector direction){
        spikeBall.setPlaying(true);
        spikeBall.removeSpikeBallFromPlayer(player);
        spikeBall.setInitialVelocity(direction);
    }

    public void removePlayer(Player player){
        if(currentState == GameStates.MATCH){
            messageAllPlayersInGame("Because " + player.getDisplayName() + " left the game, the game is cancelled!");
            SpikeBallGameHandler.removeGame(this);
        } else {
            messageAllPlayersInGame(player.getDisplayName() + " left the game!");
            redTeam.remove(player);
            blueTeam.remove(player);
        }
    }

    public boolean ifPlayerIsInGame(Player player){
        for(Player p : redTeam){
            if(p.equals(player)){
                return true;
            }
        }

        for(Player d : blueTeam){
            if(d.equals(player)){
                return true;
            }
        }
        return false;
    }

    public SpikeBallNet getSpikeBallNet(){
        return spikeBallNet;
    }

    public boolean addPlayer(Player p){
        if(redTeam.size() + blueTeam.size() <= 3){
            if(redTeam.size() == 2){
                blueTeam.add(p);
            } else if(blueTeam.size() == 2){
                redTeam.add(p);
            } else {
                Random random = new Random();
                int randomNumber = random.nextInt(2);
                if(randomNumber == 0){
                    redTeam.add(p);
                } else {
                    blueTeam.add(p);
                }
            }
            return true;
        }
        return false;
    }

    public void messageAllPlayersInGame(String message){
        for(Player p : redTeam){
            SpikeBallMain.sendPluginMessage(p, message);
        }
        for (Player c : blueTeam){
            SpikeBallMain.sendPluginMessage(c, message);
        }
    }

}
