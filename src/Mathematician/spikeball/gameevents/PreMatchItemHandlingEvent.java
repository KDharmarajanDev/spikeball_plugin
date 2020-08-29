package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.cooldown.Cooldown;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PreMatchItemHandlingEvent implements Listener {

    @EventHandler
    public void playerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block.getType().equals(Material.SCAFFOLDING)){
            SpikeBallNet possibleNet = SpikeBallGameHandler.getSpikeBallNet(block);
            if(possibleNet != null){
                SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                if(playerSpikeBallGame != null && !playerSpikeBallGame.getSpikeBallNet().equals(possibleNet) && event.getItem() == null){
                    playerSpikeBallGame.removePlayer(player);
                    SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                } else if(playerSpikeBallGame == null && event.getItem() == null){
                    SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                }

            }
        } else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
            if(playerSpikeBallGame != null && !playerSpikeBallGame.isInProgress()){
                ItemStack item = event.getItem();
                if(item != null) {
                    if (item.getType().equals(Material.RED_CONCRETE) || item.getType().equals(Material.BLUE_CONCRETE)) {
                        playerSpikeBallGame.switchTeams(player, item.getType().equals(Material.RED_CONCRETE));
                    } else if (item.getType().equals(Material.CLOCK)) {
                        if (!CooldownHandler.containsCooldown(player.getDisplayName() + " Leave Clock")) {

                            CooldownHandler.addCooldown(new Cooldown(player.getDisplayName() + " Leave Clock", 3000, input -> {
                                playerSpikeBallGame.safeRemovePlayer(player);
                            }));

                            SpikeBallMain.sendPluginMessage(player, "You will be leaving this game in 3 seconds. Right-click to cancel.");
                        } else {
                            SpikeBallMain.sendPluginMessage(player, "You have cancelled leaving.");
                            CooldownHandler.removeCooldown(player);
                        }
                    } else if(item.getType().equals(Material.RED_TERRACOTTA)){
                        playerSpikeBallGame.makePlayerReady(player);
                    } else if(item.getType().equals(Material.GREEN_TERRACOTTA)){
                        playerSpikeBallGame.makePlayerNotReady(player);
                    }
                }
            }
        }
    }

}
