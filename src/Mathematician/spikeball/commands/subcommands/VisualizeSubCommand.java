package Mathematician.spikeball.commands.subcommands;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import org.bukkit.entity.Player;

public class VisualizeSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(Player player, String[] args) {
        SpikeBallGame playerGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if (playerGame != null) {
            playerGame.visualizeSpikeBallNetBoundingBox();
            SpikeBallMain.sendPluginMessage(player, "Successfully visualizing.");
        } else {
            SpikeBallMain.sendPluginMessage(player, "Join a game in order to visualize.");
        }
        return true;
    }
}
