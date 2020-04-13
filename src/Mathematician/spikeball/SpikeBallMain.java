package Mathematician.spikeball;

import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gamemechanics.GameEventHandler;
import Mathematician.spikeball.gamemechanics.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.powerups.CooldownHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SpikeBallMain extends JavaPlugin {

    private CommandHandler commandHandler = new CommandHandler();
    public static JavaPlugin plugin;
    private static String pluginInformationString = ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Spike Ball" + ChatColor.DARK_GREEN + "]" +ChatColor.DARK_GRAY +" » " + ChatColor.GOLD;
    public static BukkitScheduler scheduler;

    @Override
    public void onEnable(){
        //Initial Information
        plugin = this;
        getLogger().info("Running Spike Ball Plugin!");

        //Events Registering
        getServer().getPluginManager().registerEvents(new GameEventHandler(), this);

        //Command Registering
        getCommand("spikeball").setExecutor(new CommandHandler());

        scheduler = getServer().getScheduler();
        plugin = this;
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                SpikeBallGameHandler.updateAll();
                CooldownHandler.update();
            }
        }, 0L, 2L);
    }

    @Override
    public void onDisable(){
        //Fired when the server stops and disables all plugins
    }

    public static void sendPluginMessage(Player player, String message){
        player.sendMessage(pluginInformationString + message);
    }

}
