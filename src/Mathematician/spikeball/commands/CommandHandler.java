package Mathematician.spikeball.commands;

import Mathematician.spikeball.commands.subcommands.SubCommandKeys;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spikeball")) {
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length >= 1){
                    for(SubCommandKeys subCommandKeys : SubCommandKeys.values()){
                        if(subCommandKeys.getValue().equalsIgnoreCase(args[0])){
                            try {
                                return subCommandKeys.getSubCommandClass().newInstance().onSubCommand(player, args);
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
