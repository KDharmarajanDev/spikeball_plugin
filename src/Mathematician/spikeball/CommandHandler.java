package Mathematician.spikeball;

import Mathematician.spikeball.gamemechanics.SpikeBallGame;
import Mathematician.spikeball.gamemechanics.SpikeBallGameHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spikeball")) {
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length >= 1){
                    if(args[0].equalsIgnoreCase("setup")){
                        SpikeBallMain.sendPluginMessage(player,"Please select the Spike Ball net by clicking on it with the special tool.");
                        ItemStack tool = new ItemStack(Material.STICK);
                        ItemMeta toolIteMMeta = tool.getItemMeta();
                        toolIteMMeta.setDisplayName(ChatColor.GOLD + "Spike Ball Creator Tool");
                        tool.setItemMeta(toolIteMMeta);
                        player.getInventory().setItemInMainHand(tool);
                        return true;
                    } else if(args[0].equalsIgnoreCase("leave")){
                        SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                        if(spikeBallGame != null){
                            spikeBallGame.removePlayer(player);
                        } else {
                            SpikeBallMain.sendPluginMessage(player,"You are not in a game!");
                        }
                        return true;
                    } else if(args[0].equalsIgnoreCase("start")){
                        SpikeBallGame playerGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                        if(playerGame != null){
                            playerGame.startGame();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
