package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import Mathematician.spikeball.gamemechanics.powerups.PowerUp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class GameMechanicEvent implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if(spikeBallGame != null){
            if(spikeBallGame.isInProgress()){
                event.setCancelled(true);
                ItemStack droppedItem = event.getItemDrop().getItemStack();
                if(droppedItem != null) {
                    if ((droppedItem.getType().equals(Material.RED_CONCRETE) || droppedItem.getType().equals(Material.BLUE_CONCRETE))) {
                        spikeBallGame.switchTypeOfHit(player, event.getItemDrop().getItemStack());
                    } else {
                        PowerUp[] powerUps = spikeBallGame.getPowerUps(player);
                        if(powerUps != null){
                            for(PowerUp powerUp : powerUps){
                                if(droppedItem.isSimilar(powerUp.getItemStack())){
                                    double timeRemaining = CooldownHandler.getTimeRemaining(player, powerUp.getTag());
                                    if(timeRemaining <= 0){
                                        boolean ifApplied = powerUp.applyEffect();
                                        if(!ifApplied){
                                            SpikeBallMain.sendPluginMessage(player, "Unable to apply power up!");
                                        }
                                    } else {
                                        SpikeBallMain.sendPluginMessage(player, ChatColor.GOLD + "Sorry, but you have a cool down for " + ChatColor.AQUA + timeRemaining + ChatColor.GOLD + " seconds!");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
