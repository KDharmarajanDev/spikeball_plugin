package Mathematician.spikeball.commands.subcommands;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import org.bukkit.entity.Player;

public class StartSubCommand implements SubCommand {

    @Override
    public boolean onSubCommand(Player player, String[] args) {
        SpikeBallGame playerGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if(playerGame != null){
            if(!playerGame.isInProgress()){
                playerGame.startGame();
            } else {
                SpikeBallMain.sendPluginMessage(player,"The game has already started!");
            }
        } else {
            SpikeBallMain.sendPluginMessage(player,"You are not in a game!");
        }
        return true;
    }
}
