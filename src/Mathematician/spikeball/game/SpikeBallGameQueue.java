package Mathematician.spikeball.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.PriorityQueue;

public class SpikeBallGameQueue {

    private HashMap<String, ItemStack[]> inventories;
    private PriorityQueue<Player> playerQueue;

    public SpikeBallGameQueue(){
        inventories = new HashMap<>();
        playerQueue = new PriorityQueue<>();
    }

}
