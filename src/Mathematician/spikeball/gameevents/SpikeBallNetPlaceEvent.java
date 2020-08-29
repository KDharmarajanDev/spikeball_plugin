package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpikeBallNetPlaceEvent implements Listener {

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(SpikeBallGameHandler.getGamePlayerIsIn(player) != null){
            event.setCancelled(true);
        }
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemStack spikeballItem = new ItemStack(Material.SCAFFOLDING);
        ItemMeta netMeta = spikeballItem.getItemMeta();
        netMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spike Ball Net");
        spikeballItem.setItemMeta(netMeta);
        Block block = event.getBlock();
        if(itemInHand != null && itemInHand.getItemMeta() != null && spikeballItem.getItemMeta() !=null && itemInHand.getItemMeta().getDisplayName().equals(spikeballItem.getItemMeta().getDisplayName())){
            SpikeBallNet addingSpikeBallNet = new SpikeBallNet(block);
            if(SpikeBallNet.ifUsingTexturePack){
                net.minecraft.server.v1_15_R1.ItemStack spikeBallNetRepresentation = CraftItemStack.asNMSCopy(spikeballItem);
                NBTTagCompound nbtData = (spikeBallNetRepresentation.hasTag()) ? spikeBallNetRepresentation.getTag() : new NBTTagCompound();
                nbtData.setInt("CustomModelData",150000);
                spikeBallNetRepresentation.setTag(nbtData);
                spikeballItem = CraftItemStack.asBukkitCopy(spikeBallNetRepresentation);
                ArmorStand spikeBallNet = (ArmorStand) player.getWorld().spawnEntity(block.getLocation().add(0.5,0,0.5), EntityType.ARMOR_STAND);
                spikeBallNet.setVisible(false);
                spikeBallNet.setSilent(true);
                spikeBallNet.setCollidable(true);
                spikeBallNet.setCanPickupItems(false);
                spikeBallNet.setHelmet(spikeballItem);
                addingSpikeBallNet = new SpikeBallNet(spikeBallNet);

                net.minecraft.server.v1_15_R1.Entity nbtArmorStand = ((CraftEntity) spikeBallNet).getHandle();
                NBTTagCompound nbtDataCompound = new NBTTagCompound();
                nbtArmorStand.c(nbtDataCompound);
                nbtDataCompound.setInt("DisabledSlots", 2039583);
                EntityLiving spikeBallNetEntity = (EntityLiving) nbtArmorStand;
                spikeBallNetEntity.a(nbtDataCompound);
                event.setCancelled(true);
            }
            SpikeBallGameHandler.addSpikeBallNet(addingSpikeBallNet);
            SpikeBallMain.saveNet(block.getLocation());
            SpikeBallMain.sendPluginMessage(player, "This is now a valid Spike Ball Net.");
        }
    }

}
