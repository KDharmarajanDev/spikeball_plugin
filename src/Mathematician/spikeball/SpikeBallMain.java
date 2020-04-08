package Mathematician.spikeball;

import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gamemechanics.GameEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpikeBallMain extends JavaPlugin {

    private CommandHandler commandHandler = new CommandHandler();
    private static SpikeBallMain plugin;
    private static String pluginInformationString = "&";

    @Override
    public void onEnable(){
        //Initial Information
        plugin = this;
        getLogger().info("Running Spike Ball Plugin!");

        //Events Registering
        getServer().getPluginManager().registerEvents(new GameEventHandler(), this);

        //Command Registering
        getCommand("spikeball").setExecutor(new CommandHandler());
    }


    @Override
    public void onDisable(){
        //Fired when the server stops and disables all plugins
    }

    public static SpikeBallMain getPlugin(){
        return plugin;
    }

    public static void sendPluginMessage(Player player){
        player.sendMessage("");
    }

}
