package Mathematician.spikeball.gamemechanics.powerups;

import org.bukkit.entity.Player;

public class Cooldown {

    private PowerUp powerUp;
    private long finishTime;
    private String tag;

    public Cooldown(PowerUp powerUp, long coolDownTime, String tag){
        this.powerUp = powerUp;
        this.finishTime = System.currentTimeMillis() + coolDownTime;
        this.tag = tag;
    }

    public Cooldown(PowerUp powerUp, long coolDownTime){
        this.powerUp = powerUp;
        this.finishTime = System.currentTimeMillis() + coolDownTime;
        this.tag = powerUp.getTag();
    }

    public double getTimeRemainingInSeconds(){
        return (finishTime - System.currentTimeMillis()) / 1000.0;
    }

    public PowerUp getPowerUp(){
        return powerUp;
    }

    public boolean isCompleted(){
        return finishTime - System.currentTimeMillis() <= 0;
    }

    public String getTag(){
        return tag;
    }
}
