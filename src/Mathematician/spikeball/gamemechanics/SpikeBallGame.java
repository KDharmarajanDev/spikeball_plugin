package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.ChatColor;
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
        if(numPlayers() == 4) {
            currentState = GameStates.MATCH;
            Random random = new Random();
            int randomInt = random.nextInt(redTeam.size());
            spikeBall.giveToPlayer(redTeam.get(randomInt));
        } else {
            currentState = GameStates.PRACTICE;
            if(redTeam.size() >= 1){
                spikeBall.giveToPlayer(redTeam.get(0));
            } else if(blueTeam.size() >= 1) {
                spikeBall.giveToPlayer(blueTeam.get(0));
            }
        }
    }

    public void serveSpikeBall(Player player, Vector direction){
        spikeBall.setPlaying(true);
        spikeBall.spawnInLocation(player.getLocation(), direction);
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

    public void changeSpikeBallVelocity(Vector velocity){
        spikeBall.setInitialVelocity(velocity);
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
        if(!ifPlayerIsInGame(p)) {
            if (redTeam.size() + blueTeam.size() <= 3) {
                if (redTeam.size() == 2) {
                    blueTeam.add(p);
                    SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.BLUE + "Blue Team " + ChatColor.GOLD + ".");
                } else if (blueTeam.size() == 2) {
                    redTeam.add(p);
                    SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.RED + "Red Team " + ChatColor.GOLD + ".");
                } else {
                    Random random = new Random();
                    int randomNumber = random.nextInt(2);
                    if (randomNumber == 0) {
                        redTeam.add(p);
                        SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.RED + "Red Team " + ChatColor.GOLD + ".");
                    } else {
                        blueTeam.add(p);
                        SpikeBallMain.sendPluginMessage(p, "You have joined a game with this Spike Ball Net on the " + ChatColor.BLUE + "Blue Team " + ChatColor.GOLD + ".");
                    }
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

    public int numPlayers(){
        return redTeam.size() + blueTeam.size();
    }

}
