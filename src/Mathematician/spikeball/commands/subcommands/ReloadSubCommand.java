package Mathematician.spikeball.commands.subcommands;

import Mathematician.spikeball.SpikeBallMain;
import org.bukkit.entity.Player;

public class ReloadSubCommand implements SubCommand{

    @Override
    public boolean onSubCommand(Player player, String[] args) {
        SpikeBallMain.plugin.reloadConfig();
        SpikeBallMain.plugin.saveConfig();
        SpikeBallMain.loadNets();
        SpikeBallMain.sendPluginMessage(player, "Configuration Reloaded!");
        return true;
    }
}
