package Mathematician.spikeball.gameelements;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

public class SpikeBall {

    private Slime ball;
    private World world;

    private boolean ifPlaying = false;

    public SpikeBall(World world){
        this.world = world;
    }

    public Location getLocation(){
        return ball.getLocation();
    }

    public boolean isPlaying(){
        return ifPlaying;
    }

    public void setPlaying(boolean ifPlaying){
        this.ifPlaying = ifPlaying;
    }

    public void giveToPlayer(Player target){
        ItemStack slimeHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta slimeHeadMeta = (SkullMeta) slimeHead.getItemMeta();

        slimeHeadMeta.setDisplayName("§2Spike Ball");
        slimeHeadMeta.setOwner("MHF_Slime");

        slimeHead.setItemMeta(slimeHeadMeta);

        target.getInventory().setItemInMainHand(slimeHead);

        ifPlaying = false;
    }

    public void removeSpikeBallFromPlayer(Player target){
        target.getInventory().remove(Material.PLAYER_HEAD);
    }

    public void spawnInLocation(Location location){
        LivingEntity mob = (LivingEntity) world.spawnEntity(location, EntityType.SLIME);
        ball = (Slime) mob;
        ball.setSize(1);
        ball.setCustomName("§2Spike Ball");

        ball.setCanPickupItems(false);
        ball.setCustomNameVisible(true);
        ball.setInvulnerable(true);
        ball.setSilent(true);
        ball.setAI(false);
        ball.setGravity(true);

        setPlaying(true);
    }

    public void spawnInLocation(Location location, Vector movement){
        LivingEntity mob = (LivingEntity) world.spawnEntity(location, EntityType.SLIME);
        ball = (Slime) mob;
        ball.setSize(1);
        ball.setCustomName("§2Spike Ball");

        ball.setCanPickupItems(false);
        ball.setCustomNameVisible(true);
        ball.setInvulnerable(true);
        ball.setSilent(true);
        ball.setAI(false);
        ball.setGravity(true);

        ball.setVelocity(movement);
        setPlaying(true);
    }

    public void removeSpikeBallFromWorld(){
        if(ball != null){
            ball.remove();
        }
    }

    public void setInitialVelocity(Vector initialVelocity){
        if(ball != null){
            ball.setVelocity(initialVelocity);
        }
    }
}
