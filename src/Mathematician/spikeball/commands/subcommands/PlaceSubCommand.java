package Mathematician.spikeball.commands.subcommands;

import Mathematician.spikeball.SpikeBallMain;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceSubCommand implements SubCommand{

    @Override
    public boolean onSubCommand(Player player, String[] args) {
        SpikeBallMain.sendPluginMessage(player,"Please place the spike ball net!");
        ItemStack spikeballItem = new ItemStack(Material.SCAFFOLDING);
        ItemMeta netMeta = spikeballItem.getItemMeta();
        netMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spike Ball Net");
        spikeballItem.setItemMeta(netMeta);
        net.minecraft.server.v1_15_R1.ItemStack spikeBallNetRepresentation = CraftItemStack.asNMSCopy(spikeballItem);
        NBTTagCompound nbtData = (spikeBallNetRepresentation.hasTag()) ? spikeBallNetRepresentation.getTag() : new NBTTagCompound();
        nbtData.setInt("CustomModelData",150000);
        spikeBallNetRepresentation.setTag(nbtData);
        spikeballItem = CraftItemStack.asBukkitCopy(spikeBallNetRepresentation);
        player.getInventory().setItemInMainHand(spikeballItem);
        return true;
    }
}
