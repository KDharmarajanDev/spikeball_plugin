package Mathematician.spikeball.gamemechanics;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;


import java.util.ArrayList;
import java.util.List;

public class GameEventHandler implements Listener {

    //Setup Events
    @EventHandler
    public void playerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block.getType().equals(Material.SCAFFOLDING)){
            ItemStack tool = new ItemStack(Material.STICK);
            ItemMeta toolIteMMeta = tool.getItemMeta();
            toolIteMMeta.setDisplayName("&6Spike Ball Creator Tool");
            tool.setItemMeta(toolIteMMeta);

            if(event.getItem().isSimilar(tool)){
                SpikeBallGameHandler.addSpikeBallNet(new SpikeBallNet(block));
            } else {
                if(SpikeBallGameHandler.getGamePlayerIsIn(player) == null){

                }
            }
        }
    }

    //Gameplay Events
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack slimeHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta slimeHeadMeta = (SkullMeta) slimeHead.getItemMeta();

            slimeHeadMeta.setDisplayName("§2Spike Ball");
            slimeHeadMeta.setOwner("MHF_Slime");

            slimeHead.setItemMeta(slimeHeadMeta);

            if(player.getItemInHand().isSimilar(slimeHead)){

                Location playerLocation = player.getLocation();
                SpikeBallGame playerSpikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                if(playerSpikeBallGame != null){
                    playerSpikeBallGame.serveSpikeBall(player, playerLocation.getDirection());
                }
            }
        }
    }

}
