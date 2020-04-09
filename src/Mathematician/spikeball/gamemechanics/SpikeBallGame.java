package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class SpikeBallGame {

    private SpikeBall spikeBall;
    private SpikeBallNet spikeBallNet;

    private ArrayList<Player> redTeam;
    private ArrayList<Player> blueTeam;

    private Objective scoreObjective;

    private enum GameStates {
        PRACTICE, PRE_MATCH, MATCH
    }

    private GameStates currentState = GameStates.PRE_MATCH;

    private Scoreboard scoreboard;
    private ScoreboardManager scoreboardManager;

    private boolean redLastHit = true;
    private Player lastServedPlayer;
    private int hitCount = 0;

    private final double SPIKE_BALL_GROUND_TOUCHING_TOLERANCE = 0.10;

    public SpikeBallGame(Block spikeBallNet, ArrayList<Player> players){
        redTeam = new ArrayList<>();
        blueTeam = new ArrayList<>();
        this.spikeBallNet = new SpikeBallNet(spikeBallNet);
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();

        scoreObjective = scoreboard.registerNewObjective("Spike Ball Score", "dummy", ChatColor.DARK_GREEN + "Spike Ball Score");
        scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreObjective.setDisplayName(ChatColor.DARK_GREEN + "Spike Ball Score");

        Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
        redScore.setScore(0);

        Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
        blueScore.setScore(0);

        if(!(players.size() < 1)){
            for(Player p : players){
                addPlayer(p);
            }
            spikeBall = new SpikeBall(players.get(0).getWorld());
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
        lastServedPlayer = player;
        spikeBall.spawnInLocation(player.getLocation().add(player.getLocation().getDirection().normalize()).add(0,1,0), direction.normalize().multiply(4));
    }

    public void removePlayer(Player player){
        if(currentState == GameStates.MATCH || numPlayers() - 1 <= 0){
            messageAllPlayersInGame("Because " + player.getDisplayName() + " left the game, the game is cancelled!");
            removeAllScoreboards();
            SpikeBallGameHandler.removeGame(this);
        } else {
            messageAllPlayersInGame(player.getDisplayName() + " left the game!");
            redTeam.remove(player);
            blueTeam.remove(player);
            player.setScoreboard(scoreboardManager.getNewScoreboard());
        }
    }

    public void removeAllScoreboards(){
        for(Player p : redTeam){
            p.setScoreboard(scoreboardManager.getNewScoreboard());
        }

        for(Player player : blueTeam){
            player.setScoreboard(scoreboardManager.getNewScoreboard());
        }
    }

    public void changeSpikeBallVelocity(Vector velocity){
        spikeBall.setVelocity(velocity);
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
            p.setScoreboard(scoreboard);
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

    public void update(){
        switch (currentState){
            case MATCH:
                if(spikeBall.getEntity() != null && spikeBall.isPlaying()) {
                    if (spikeBall.getEntity().getLocation().add(0, -SPIKE_BALL_GROUND_TOUCHING_TOLERANCE, 0).getBlock().equals(spikeBallNet.toBlock())) {
                        spikeBall.setVelocity(new Vector(spikeBall.getEntity().getVelocity().getX(), -1 * spikeBall.getEntity().getVelocity().getY(), spikeBall.getEntity().getVelocity().getZ()));
                    } else if (!spikeBall.getEntity().getLocation().add(0, -SPIKE_BALL_GROUND_TOUCHING_TOLERANCE, 0).getBlock().getType().equals(Material.AIR)) {
                        updateScore();
                    }
                }
                break;
            case PRACTICE:
                if(spikeBall.getEntity() != null && spikeBall.isPlaying()) {
                    if (spikeBall.getEntity().getLocation().add(0, -SPIKE_BALL_GROUND_TOUCHING_TOLERANCE, 0).getBlock().equals(spikeBallNet.toBlock())) {
                        spikeBall.setVelocity(new Vector(spikeBall.getEntity().getVelocity().getX(), -1 * spikeBall.getEntity().getVelocity().getY(), spikeBall.getEntity().getVelocity().getZ()));
                    } else if (!spikeBall.getEntity().getLocation().add(0, -SPIKE_BALL_GROUND_TOUCHING_TOLERANCE, 0).getBlock().getType().equals(Material.AIR)) {
                        updateScore();
                    }
                }
                break;
        }
    }

    public void clearSpikeBall(){
        spikeBall.removeSpikeBallFromWorld();
        for(Player p : redTeam){
            for(ItemStack itemStack : p.getInventory().getContents()){
                if(itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                    itemStack.setAmount(0);
                    break;
                }
            }
        }
        for(Player c : blueTeam){
            for(ItemStack itemStack : c.getInventory().getContents()){
                if(itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                    itemStack.setAmount(0);
                    break;
                }
            }
        }
    }

    public boolean isInProgress(){
        if(currentState == GameStates.MATCH||currentState == GameStates.PRACTICE){
            return true;
        }
        return false;
    }

    //Activates when a team fails
    public void updateScore(){
        if(redLastHit){
            Score blueScore = scoreObjective.getScore(ChatColor.DARK_BLUE + "Blue Team:");
            blueScore.setScore(blueScore.getScore() + 1);
            if(blueTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(blueTeam.size());
                spikeBall.giveToPlayer(blueTeam.get(randomInt));
            } else if(lastServedPlayer != null){
                spikeBall.giveToPlayer(lastServedPlayer);
            } else if(redTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(redTeam.size());
                spikeBall.giveToPlayer(redTeam.get(randomInt));
            }
        } else {
            Score redScore = scoreObjective.getScore(ChatColor.DARK_RED + "Red Team:");
            redScore.setScore(redScore.getScore() + 1);
            if(redTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(redTeam.size());
                spikeBall.giveToPlayer(redTeam.get(randomInt));
            } else if(lastServedPlayer != null){
                spikeBall.giveToPlayer(lastServedPlayer);
            } else if(blueTeam.size() > 0){
                Random random = new Random();
                int randomInt = random.nextInt(blueTeam.size());
                spikeBall.giveToPlayer(blueTeam.get(randomInt));
            }
        }
        spikeBall.removeSpikeBallFromWorld();
    }

}
