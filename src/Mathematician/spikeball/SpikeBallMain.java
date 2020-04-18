package Mathematician.spikeball;

import Mathematician.spikeball.advancedparticles.AdvancedParticleGenerator;
import Mathematician.spikeball.advancedparticles.AdvancedParticleHandler;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.gamemechanics.GameEventHandler;
import Mathematician.spikeball.gamemechanics.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.powerups.CooldownHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SpikeBallMain extends JavaPlugin {

    private CommandHandler commandHandler = new CommandHandler();
    public static JavaPlugin plugin;
    private static String pluginInformationString = ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Spike Ball" + ChatColor.DARK_GREEN + "]" +ChatColor.DARK_GRAY +" » " + ChatColor.GOLD;
    public static BukkitScheduler scheduler;
    public static int numSpikeBallNets = 0;

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
                AdvancedParticleHandler.update();
            }
        }, 0L, 1L);
        loadConfiguration();
        loadNets();
    }

    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void loadNets(){
        numSpikeBallNets = getConfig().getInt("Spike Ball.Number of Nets");
        if(numSpikeBallNets >= 0){
            for(int i = 0; i < numSpikeBallNets; i++){
                Location location = getConfig().getLocation("Spike Ball.Net Locations." + i);
                if(location != null){
                    if(location.getBlock().getType().equals(Material.SCAFFOLDING)) {
                        SpikeBallGameHandler.addSpikeBallNet(new SpikeBallNet(location.getBlock()));
                    } else {
                        getLogger().info("There is no Scaffolding Block at " + location.toString());
                    }
                } else {
                    getLogger().info("Net Location " + i + " doesn't exist!");
                }
            }
        } else {
            getLogger().info("Number of Spike Ball Nets is a Negative Number!");
        }
    }

    public static void saveNet(Location location){
        plugin.getConfig().set("Spike Ball.Net Locations." + (numSpikeBallNets),location);
        numSpikeBallNets++;
        plugin.saveConfig();
    }

    @Override
    public void onDisable(){
        getConfig().set("Spike Ball.Number of Nets", SpikeBallGameHandler.getNumNets());
        saveConfig();
    }

    public static void sendPluginMessage(Player player, String message){
        player.sendMessage(pluginInformationString + message);
    }

}
