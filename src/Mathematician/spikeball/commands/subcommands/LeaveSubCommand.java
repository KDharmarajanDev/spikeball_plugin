package Mathematician.spikeball.commands.subcommands;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import org.bukkit.entity.Player;

public class LeaveSubCommand implements SubCommand{

    @Override
    public boolean onSubCommand(Player player, String[] args) {
        SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if(spikeBallGame != null){
            spikeBallGame.removePlayer(player);
        } else {
            SpikeBallMain.sendPluginMessage(player,"You are not in a game!");
        }
        return true;
    }
}
