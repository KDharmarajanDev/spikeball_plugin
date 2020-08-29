package Mathematician.spikeball.gamemechanics.powerups;

import Mathematician.spikeball.SpikeBallMain;
import Mathematician.spikeball.gameelements.SpikeBall;
import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.gamemechanics.cooldown.CooldownHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ShadowRealm implements PowerUp{

    private String tag = "Shadow Realm";
    private ItemStack representativeItem;
    private Player player;
    private boolean hasStarted;
    private SpikeBallGame spikeBallGame;
    private boolean isCompleted;



    public ShadowRealm(Player player, SpikeBallGame spikeBallGame){
        this.player = player;
        representativeItem = new ItemStack(Material.NETHER_PORTAL);
        ItemMeta representativeItemMeta = representativeItem.getItemMeta();
        representativeItemMeta.setDisplayName(ChatColor.DARK_GRAY + "Shadow Realm Power Up");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "About to touch the ground?");
        lore.add(ChatColor.GOLD + "Activate this power up to suck the ball into the " + ChatColor.DARK_GRAY + "Shadow Realm!");
        representativeItemMeta.setLore(lore);
        representativeItem.setItemMeta(representativeItemMeta);
        hasStarted = false;
        isCompleted = false;
        this.spikeBallGame = spikeBallGame;
    }

    public boolean applyEffect() {
        if(spikeBallGame != null && ((spikeBallGame.onBlueTeam(player) && !spikeBallGame.getRedLastHit()) || (spikeBallGame.onRedTeam(player) && spikeBallGame.getRedLastHit())) && !spikeBallGame.isIfPowerUpGoingOn()){
            SpikeBall spikeBall = spikeBallGame.getSpikeBall();
            if(spikeBall != null && spikeBall.getEntity() != null && spikeBall.isPlaying()){
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

                }
            }
            if(isCompleted()){
                reset();
            }
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Player getPlayer(){
        return player;
    }

    public void reset(){
        hasStarted = false;
        isCompleted = false;
        spikeBallGame.setLastUsedPowerUp(null);
    }

    public SpikeBallGame getSpikeBallGame() {
        return spikeBallGame;
    }

    public void visualizePortals(){

    }
}
