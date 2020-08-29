package Mathematician.spikeball;

import Mathematician.spikeball.advancedparticles.AdvancedParticleHandler;
import Mathematician.spikeball.commands.CommandHandler;
import Mathematician.spikeball.gameelements.SpikeBallNet;
import Mathematician.spikeball.gameevents.*;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpikeBallMain extends JavaPlugin {

    private CommandHandler commandHandler = new CommandHandler();
    public static SpikeBallMain plugin;
    private static String pluginInformationString = ChatColor.DARK_GREEN + "[" + ChatColor.GOLD + "Spike Ball" + ChatColor.DARK_GREEN + "]" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD;
    public static BukkitScheduler scheduler;

    @Override
    public void onEnable() {
        //Initial Information
        plugin = this;
        getLogger().info("Running Spike Ball Plugin!");

        //Events Registering
        getServer().getPluginManager().registerEvents(new GameMechanicEvent(), this);
        getServer().getPluginManager().registerEvents(new HitSpikeBallEvent(), this);
        getServer().getPluginManager().registerEvents(new JoiningGameEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerHandlingEvents(), this);
        getServer().getPluginManager().registerEvents(new PreMatchItemHandlingEvent(), this);
        getServer().getPluginManager().registerEvents(new SpikeBallHandlingEvents(), this);
        getServer().getPluginManager().registerEvents(new SpikeBallNetPlaceEvent(), this);

        //Command Registering
        getCommand("spikeball").setExecutor(new CommandHandler());

        scheduler = getServer().getScheduler();
        plugin = this;
        loadConfiguration();
        loadNets();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                SpikeBallGameHandler.updateAll();
                CooldownHandler.update();
                AdvancedParticleHandler.update();
            }
        }, 0L, 1L);
    }

    public static void loadConfiguration() {
        if (!plugin.getConfig().contains("Spike Ball")) {
            plugin.getConfig().options().copyDefaults(true);
        }
        plugin.saveConfig();
    }

    public static void loadNets() {
        boolean ifUsingTexturePack = plugin.getConfig().getBoolean("Spike Ball.Using Texture Pack");
        SpikeBallNet.ifUsingTexturePack = ifUsingTexturePack;
        List<Location> netLocations = (List<Location>) plugin.getConfig().getList("Spike Ball.Net Locations");
        if(netLocations != null) {
            for (Location location : netLocations) {
                if (location != null) {
                    if (!ifUsingTexturePack) {
                        if (location.getBlock().getType().equals(Material.SCAFFOLDING)) {
                            SpikeBallGameHandler.addSpikeBallNet(new SpikeBallNet(location.getBlock()));
                        } else {
                            plugin.getLogger().info("There is no Scaffolding Block at " + location.toString());
                        }
                    } else {
                        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 1, 1, 1);
                        for (Entity e : entities) {
                            if (e instanceof ArmorStand) {
                                ArmorStand armorStand = (ArmorStand) e;
                                ItemStack helmet = armorStand.getHelmet();
                                if (helmet != null) {
                                    net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(helmet);
                                    NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
                                    int customModelDataNumber = compound.getInt("CustomModelData");
                                    if (customModelDataNumber == 150000) {
                                        SpikeBallGameHandler.addSpikeBallNet(new SpikeBallNet((ArmorStand) e));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    plugin.getLogger().info("Net Location " + location.toString() + " doesn't exist!");
                }
            }
        }
    }

    public static void saveNet(Location location){
        List<Location> netLocations = (List<Location>) plugin.getConfig().getList("Spike Ball.Net Locations");
        if(netLocations == null){
            netLocations = new ArrayList<>();
        }
        netLocations.add(location);
        plugin.getConfig().set("Spike Ball.Net Locations", netLocations);
        plugin.saveConfig();
    }

    public static void deleteNet(Location location){
        List<Location> netLocations = (List<Location>) plugin.getConfig().getList("Spike Ball.Net Locations");
        if(netLocations != null) {
            netLocations.remove(location);
            plugin.getConfig().set("Spike Ball.Net Locations", netLocations);
            plugin.saveConfig();
        }
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
