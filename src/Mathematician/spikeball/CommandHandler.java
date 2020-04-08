package Mathematician.spikeball;

import Mathematician.spikeball.gamemechanics.SpikeBallGame;
import Mathematician.spikeball.gamemechanics.SpikeBallGameHandler;
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
                        player.sendMessage("Please select the Spike Ball net by clicking on it with the special tool.");
                        ItemStack tool = new ItemStack(Material.STICK);
                        ItemMeta toolIteMMeta = tool.getItemMeta();
                        toolIteMMeta.setDisplayName("&6Spike Ball Creator Tool");
                        tool.setItemMeta(toolIteMMeta);
                        player.getInventory().setItemInMainHand(tool);
                    } else if(args[0].equalsIgnoreCase("leave")){
                        player.sendMessage("You have left!");
                        SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                        spikeBallGame.removePlayer(player);
                    } else if(args[0].equalsIgnoreCase("start")){
                        SpikeBallGame playerGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
                        if(playerGame != null){
                            playerGame.startGame();
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

}
