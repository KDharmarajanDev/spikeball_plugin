package Mathematician.spikeball.gameelements;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SpikeBall {

    private Slime ball;
    private World world;

    private static ItemStack slimeHead = new ItemStack(Material.PLAYER_HEAD, 1);

    private boolean ifPlaying = false;

    public SpikeBall(World world) {
        this.world = world;
    }

    public Location getLocation() {
        return ball.getLocation();
    }

    public boolean isPlaying() {
        return ifPlaying;
    }

    public void setPlaying(boolean ifPlaying) {
        this.ifPlaying = ifPlaying;
    }

    public void giveToPlayer(Player target) {
        target.getInventory().setItem(8, getSpikeBallHead());
        ifPlaying = false;
    }

    public void spawnInLocation(Location location) {
        LivingEntity mob = (LivingEntity) world.spawnEntity(location, EntityType.SLIME);
        ball = (Slime) mob;
        ball.setSize(1);
        ball.setCustomName(ChatColor.GREEN + "Spike Ball");

        ball.setCanPickupItems(false);
        ball.setCustomNameVisible(true);
        ball.setInvulnerable(true);
        ball.setSilent(true);
        ball.setAI(true);
        ball.setGravity(true);
        ball.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000,100000,false,false));

        setPlaying(true);
    }

    public void spawnInLocation(Location location, Vector movement) {
        LivingEntity mob = (LivingEntity) world.spawnEntity(location, EntityType.SLIME);
        ball = (Slime) mob;
        ball.setSize(1);
        ball.setCustomName(ChatColor.GREEN + "Spike Ball");

        ball.setCanPickupItems(false);
        ball.setCustomNameVisible(true);
        ball.setInvulnerable(true);
        ball.setSilent(true);
        ball.setAI(true);
        ball.setGravity(true);
        ball.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000,100000,false,false));

        ball.setVelocity(movement);
        setPlaying(true);
    }

    public void removeSpikeBallFromWorld() {
        if (ball != null) {
            ball.setHealth(0);
            ifPlaying = false;
        }
    }

    public void setVelocity(Vector initialVelocity) {
        if (ball != null) {
            ball.setVelocity(initialVelocity);
        }
    }

    public static ItemStack getSpikeBallHead() {
        SkullMeta slimeHeadMeta = (SkullMeta) slimeHead.getItemMeta();

        slimeHeadMeta.setDisplayName(ChatColor.GREEN + "Spike Ball");
        slimeHeadMeta.setOwner("MHF_Slime");

        slimeHead.setItemMeta(slimeHeadMeta);
        return slimeHead;
    }

    public Slime getEntity(){
        return ball;
    }
}
