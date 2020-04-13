package Mathematician.spikeball.gamemechanics.powerups;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ListIterator;

public class CooldownHandler {

    private static ArrayList<Cooldown> cooldowns = new ArrayList<>();

    public static void update(){
        ListIterator iterator = cooldowns.listIterator();
        while(iterator.hasNext()){
            Cooldown cooldown = (Cooldown) iterator.next();
            if(cooldown.isCompleted()){
                iterator.remove();
            } else {
                double timeRemaining = cooldown.getTimeRemainingInSeconds();
                if(timeRemaining > 0) {
                    cooldown.getPowerUp().getItemStack().setAmount(Math.min((int) Math.ceil(timeRemaining), 64));
                } else {
                    cooldown.getPowerUp().getItemStack().setAmount(1);
                }
                cooldown.getPowerUp().getSpikeBallGame().givePowerUpsToPlayer(cooldown.getPowerUp().getPlayer());
            }
        }
    }

    public static double getTimeRemaining(Player player, String tag){
        for(Cooldown cooldown : cooldowns){
            if(cooldown.getTag().equalsIgnoreCase(tag) && cooldown.getPowerUp().getPlayer().equals(player)) {
                if (cooldown.isCompleted()) {
                    return 0;
                } else {
                    return cooldown.getTimeRemainingInSeconds();
                }
            }
        }
        return 0;
    }

    public static boolean hasCooldown(Player player, String tag){
        if(getTimeRemaining(player, tag) <= 0){
            return false;
        }
        return true;
    }

    public static void addCooldown(PowerUp powerUp, long duration){
        cooldowns.add(new Cooldown(powerUp, duration));
    }
}
