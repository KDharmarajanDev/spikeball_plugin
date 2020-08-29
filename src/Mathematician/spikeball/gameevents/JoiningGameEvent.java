package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class JoiningGameEvent implements Listener {

    @EventHandler
    public void playerClickArmorStand(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        if(clickedEntity != null && clickedEntity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) clickedEntity;
            net.minecraft.server.v1_15_R1.ItemStack spikeBallNetRepresentation = CraftItemStack.asNMSCopy(armorStand.getHelmet());
            NBTTagCompound nbtData = (spikeBallNetRepresentation.hasTag()) ? spikeBallNetRepresentation.getTag() : new NBTTagCompound();
            if (nbtData.getInt("CustomModelData") == 150000) {
                SpikeBallNet possibleNet = SpikeBallGameHandler.getSpikeBallNet(armorStand);
                if (possibleNet != null) {
                    SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                    if (playerSpikeBallGame != null) {
                        if (playerSpikeBallGame.isInProgress() && player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                            player.getInventory().getItemInMainHand().setAmount(0);
                            playerSpikeBallGame.serveSpikeBall(player, player.getLocation().getDirection());
                        } else if(!playerSpikeBallGame.getSpikeBallNet().equals(possibleNet)) {
                            playerSpikeBallGame.removePlayer(player);
                            SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                        }
                    } else {
                        if(player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "" + ChatColor.BOLD + "Spike Ball Net")) {
                            SpikeBallGameHandler.removeSpikeBallNet(possibleNet);
                            armorStand.setHealth(0);
                        } else {
                            SpikeBallGameHandler.addPlayerToGameFromNet(possibleNet, player);
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }

}
