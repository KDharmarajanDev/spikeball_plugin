package Mathematician.spikeball.gameevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class SpikeBallHandlingEvents implements Listener {

    @EventHandler
    public void spikeBallTargetPlayerEvent(EntityTargetLivingEntityEvent event){
        if (event.getEntity() instanceof Slime && event.getTarget() instanceof Player){
            Slime slime = (Slime) event.getEntity();
            if(slime.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void spikeBallDeathEvent(final EntityDeathEvent event) {
        Entity deadEntity = event.getEntity();
        if(deadEntity !=null && deadEntity instanceof Slime && deadEntity.getCustomName().equalsIgnoreCase(ChatColor.GREEN + "Spike Ball")) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

}
