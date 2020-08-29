package Mathematician.spikeball.game;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ListIterator;

public class SpikeBallGameHandler {

    private static ArrayList<SpikeBallGame> games = new ArrayList<>();

    private static ArrayList<SpikeBallNet> nets = new ArrayList<>();

    public ArrayList<SpikeBallGame> getGames(){
        return games;
    }

    public synchronized static void addGame(SpikeBallGame game){
        games.add(game);
    }

    public synchronized static void removeGame(SpikeBallGame game){
        game.clearSpikeBall();
        games.remove(game);
    }

    public synchronized static SpikeBallGame getGamePlayerIsIn(Player player){
        for(SpikeBallGame game : games){
            if(game.ifPlayerIsInGame(player)){
                return game;
            }
        }
        return null;
    }

    public synchronized static void addSpikeBallNet(SpikeBallNet spikeBallNet){
        nets.add(spikeBallNet);
    }

    public static boolean containsSpikeBallNet(SpikeBallNet spikeBallNet){
        for(SpikeBallNet net : nets){
            if(net.getSpikeBallNetLocation().getBlock().equals(spikeBallNet.getSpikeBallNetLocation().getBlock())){
                return true;
            }
        }
        return false;
    }

    public synchronized static SpikeBallNet getSpikeBallNet(Block block){
        for(SpikeBallNet net : nets){
            if(net.toBlock().equals(block)){
                return net;
            }
        }
        return null;
    }

    public synchronized static SpikeBallNet getSpikeBallNet(ArmorStand armorStand){
        for(SpikeBallNet net : nets){
            if(net.getSpikeBallNetHolder().equals(armorStand)){
                return net;
            }
        }
        return null;
    }


    public synchronized static void addPlayerToGameFromNet(SpikeBallNet spikeBallNet, Player player){
        boolean ifSpikeBallNetHasAGame = false;
        for(SpikeBallGame game : games){
            if(game.getSpikeBallNet().equals(spikeBallNet)){
                if(!game.addPlayer(player)){
                    ArrayList<Player> players = new ArrayList<>();
                    players.add(player);
                    addGame(new SpikeBallGame(spikeBallNet, players));
                }
                ifSpikeBallNetHasAGame = true;
                break;
            }
        }
        if(!ifSpikeBallNetHasAGame){
            ArrayList<Player> players = new ArrayList<>();
            players.add(player);
            games.add(new SpikeBallGame(spikeBallNet, players));
        }
    }

    public synchronized static void updateAll(){
        for(SpikeBallGame game : games){
            game.update();
        }
    }

    public synchronized static void removeSpikeBallNet(SpikeBallNet spikeBallNet){
        ListIterator<SpikeBallNet> iterator = nets.listIterator();
        while(iterator.hasNext()){
            SpikeBallNet candidate = iterator.next();
            if(candidate.equals(spikeBallNet)){
                SpikeBallMain.deleteNet(spikeBallNet.getSpikeBallNetLocation());
                iterator.remove();
                break;
            }
        }
    }

    public static int getNumNets(){
        return nets.size();
    }
}
