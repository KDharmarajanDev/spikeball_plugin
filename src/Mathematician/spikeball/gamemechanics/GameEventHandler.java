package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameEventHandler implements Listener {

    //Setup Events
    @EventHandler
    public void playerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block.getType().equals(Material.SCAFFOLDING)){
            ItemStack tool = new ItemStack(Material.STICK);
            ItemMeta toolIteMMeta = tool.getItemMeta();
            toolIteMMeta.setDisplayName(ChatColor.GOLD + "Spike Ball Creator Tool");
            tool.setItemMeta(toolIteMMeta);

            SpikeBallNet possibleNet = SpikeBallGameHandler.getSpikeBallNet(block);
            if(possibleNet != null){
                SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                if(playerSpikeBallGame != null && !playerSpikeBallGame.getSpikeBallNet().equals(possibleNet) && event.getItem() == null){
                    playerSpikeBallGame.removePlayer(player);
                    SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                } else if(playerSpikeBallGame == null && event.getItem() == null){
                    SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                }

            } else {
                if(event.getItem() != null && event.getItem().isSimilar(tool)){
                    SpikeBallGameHandler.addSpikeBallNet(new SpikeBallNet(block));
                    SpikeBallMain.sendPluginMessage(player, "This is now a valid Spike Ball Net.");
                }
            }
        }
    }

    //Gameplay Events
    //Protection Events
    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(SpikeBallGameHandler.getGamePlayerIsIn(player) != null){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event){
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
                    playerSpikeBallGame.serveSpikeBall(player, playerLocation.getDirection().normalize().multiply(5));
                }
            }
        }
    }

    @EventHandler
    public void playerPunchEvent(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Slime){
            Player player = (Player) event.getDamager();
            SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
            if(spikeBallGame != null){
                spikeBallGame.changeSpikeBallVelocity(player.getLocation().getDirection());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void spikeBallTargetPlayerEvent(EntityTargetLivingEntityEvent event){
        if (event.getEntity() instanceof  Slime && event.getTarget() instanceof Player){
            Slime slime = (Slime) event.getEntity();
            if(slime.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                event.setCancelled(true);
            }
        }
    }
}
