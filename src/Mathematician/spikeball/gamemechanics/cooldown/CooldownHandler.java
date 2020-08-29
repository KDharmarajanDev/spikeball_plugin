package Mathematician.spikeball.gamemechanics.cooldown;

import Mathematician.spikeball.gamemechanics.cooldown.Cooldown;
import Mathematician.spikeball.gamemechanics.powerups.PowerUp;
import Mathematician.spikeball.gamemechanics.powerups.PowerUpCooldown;
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
                cooldown.activateFunction(true);
                iterator.remove();
            } else if(cooldown instanceof PowerUpCooldown){
                PowerUpCooldown powerUpCooldown = (PowerUpCooldown) cooldown;
                double timeRemaining = cooldown.getTimeRemainingInSeconds();
                if(timeRemaining > 0) {
                    if(timeRemaining < powerUpCooldown.getPowerUp().getItemStack().getAmount()) {
                        powerUpCooldown.getPowerUp().getItemStack().setAmount((int) Math.ceil(timeRemaining));
                    }
                } else {
                    powerUpCooldown.getPowerUp().getItemStack().setAmount(1);
                }
                powerUpCooldown.getPowerUp().getSpikeBallGame().givePowerUpsToPlayer(powerUpCooldown.getPowerUp().getPlayer());
            }
        }
    }

    public static boolean containsCooldown(String tag){
        for(Cooldown cooldown : cooldowns){
            if(cooldown.getTag().equalsIgnoreCase(tag)){
                return true;
            }
        }
        return false;
    }

    public static double getTimeRemaining(Player player, String additions){
        for(Cooldown cooldown : cooldowns){
            if(cooldown.getTag().equalsIgnoreCase(player.getDisplayName() + " " + additions)) {
                if (cooldown.isCompleted()) {
                    return 0;
                } else {
                    return cooldown.getTimeRemainingInSeconds();
                }
            }
        }
        return 0;
    }

    public static void removeCooldown(Player player){
        ListIterator iterator = cooldowns.listIterator();
        while(iterator.hasNext()) {
            Cooldown cooldown = (Cooldown) iterator.next();
            if (cooldown.getTag().contains(player.getDisplayName())) {
                iterator.remove();
                break;
            }
        }
    }

    public static void addPowerUpCooldown(PowerUp powerUp, long duration){
        cooldowns.add(new PowerUpCooldown(powerUp, duration));
        powerUp.getItemStack().setAmount((int) Math.ceil(duration / 1000.0));
        powerUp.getSpikeBallGame().givePowerUpsToPlayer(powerUp.getPlayer());
    }

    public static void addCooldown(Cooldown cooldown){
        cooldowns.add(cooldown);
    }
}
