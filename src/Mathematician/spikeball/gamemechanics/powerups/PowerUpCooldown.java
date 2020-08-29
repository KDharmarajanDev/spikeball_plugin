package Mathematician.spikeball.gamemechanics.powerups;

import Mathematician.spikeball.gamemechanics.cooldown.Cooldown;

import java.util.function.Consumer;

public class PowerUpCooldown extends Cooldown {

    private PowerUp powerUp;

    public PowerUpCooldown(PowerUp powerUp, long coolDownTime) {
        super(powerUp.getPlayer().getDisplayName() + " " + powerUp.getTag(), coolDownTime);
        this.powerUp = powerUp;
    }

    public PowerUpCooldown(PowerUp powerUp, long coolDownTime, Consumer endActivationFunction) {
        super(powerUp.getPlayer().getDisplayName() + " " + powerUp.getTag(), coolDownTime, endActivationFunction);
        this.powerUp = powerUp;
    }

    public PowerUp getPowerUp(){
        return powerUp;
    }

}
