package Mathematician.spikeball.gamemechanics.powerups;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Freeze implements PowerUp {

    private String tag = "Freeze";
    private ItemStack representativeItem;
    private Player player;
    private double freezeTime = 3;
    private long timeUntilDone;
    private boolean hasStarted;
    private SpikeBallGame spikeBallGame;

    public Freeze(Player player, SpikeBallGame spikeBallGame){
        this.player = player;
        representativeItem = new ItemStack(Material.ICE);
        ItemMeta representativeItemMeta = representativeItem.getItemMeta();
        representativeItemMeta.setDisplayName(ChatColor.BLUE + "Freeze Power Up");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "When you are in trouble,");
        lore.add(ChatColor.GOLD + "drop to stop spike ball for " + freezeTime + " seconds.");
        representativeItemMeta.setLore(lore);
        representativeItem.setItemMeta(representativeItemMeta);
        timeUntilDone = 0;
        hasStarted = false;
        this.spikeBallGame = spikeBallGame;
    }

    public boolean applyEffect() {
        if(spikeBallGame != null && ((spikeBallGame.onBlueTeam(player) && !spikeBallGame.getRedLastHit()) || (spikeBallGame.onRedTeam(player) && spikeBallGame.getRedLastHit())) && !spikeBallGame.isIfPowerUpGoingOn()){
            SpikeBall spikeBall = spikeBallGame.getSpikeBall();
            if(spikeBall != null && spikeBall.getEntity() != null && spikeBall.isPlaying()){
                timeUntilDone = (long) freezeTime * 1000 + System.currentTimeMillis();
                hasStarted = true;
                new BukkitRunnable(){
                    public void run() {
                        update();
                        if(!hasStarted){
                            cancel();
                        }
                    }
                }.runTaskTimer(SpikeBallMain.plugin,0L,1L);
                CooldownHandler.addPowerUpCooldown(this,20000);
                spikeBallGame.setLastUsedPowerUp(this);
                return true;
            }
        }
        return false;
    }

    public String getTag() {
        return tag;
    }

    public ItemStack getItemStack() {
        return representativeItem;
    }

    public void update() {
        if(hasStarted){
            SpikeBall spikeBall = spikeBallGame.getSpikeBall();
            if(spikeBall != null){
                Slime spikeBallEntity = spikeBall.getEntity();
                if(spikeBallEntity !=null && spikeBall.isPlaying()){
                    spikeBallEntity.setVelocity(new Vector(0,0,0));
                    visualizeSpikeBallEntityWithFreeze();
                }
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.BLUE + "Freeze" + net.md_5.bungee.api.ChatColor.GOLD + " expires in " + net.md_5.bungee.api.ChatColor.AQUA + ((timeUntilDone - System.currentTimeMillis()) / 1000.0) + net.md_5.bungee.api.ChatColor.GOLD + " seconds."));
            if(isCompleted()){
                reset();
            }
        }
    }

    public boolean isCompleted() {
        return System.currentTimeMillis() >= timeUntilDone;
    }

    public Player getPlayer(){
        return player;
    }

    public void reset(){
        timeUntilDone = 0;
        hasStarted = false;
        spikeBallGame.setLastUsedPowerUp(null);
    }

    public SpikeBallGame getSpikeBallGame() {
        return spikeBallGame;
    }

    public void visualizeSpikeBallEntityWithFreeze(){
        Particle effect = Particle.SNOWBALL;
        World world = spikeBallGame.getSpikeBall().getEntity().getWorld();
        BoundingBox boundingBox = spikeBallGame.getSpikeBall().getEntity().getBoundingBox();
        world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()), 1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ()),1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ()),1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ()),1);

        world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ()),1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ()),1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ()),1);
        world.spawnParticle(effect, new Location(world, boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ()),1);
    }
}
