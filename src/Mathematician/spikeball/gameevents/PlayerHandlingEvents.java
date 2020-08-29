package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandlingEvents implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        SpikeBallGame game = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if(game != null){
            game.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(SpikeBallGameHandler.getGamePlayerIsIn(player) != null){
            event.setCancelled(true);
        }
    }


    @EventHandler (priority = EventPriority.HIGH)
    public void playerServeEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getItem() != null && event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                Location playerLocation = player.getLocation();
                SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                if(playerSpikeBallGame != null){
                    event.getItem().setAmount(0);
                    playerSpikeBallGame.serveSpikeBall(player, playerLocation.getDirection());
                }
            }
        }
    }


    @EventHandler
    public void inventoryMoveEvent(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(player != null){
            SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
            if(spikeBallGame != null){
                event.setCancelled(true);
            }
        }
    }

}
