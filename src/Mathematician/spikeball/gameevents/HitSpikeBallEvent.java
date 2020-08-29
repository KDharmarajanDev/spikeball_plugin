package Mathematician.spikeball.gameevents;

import Mathematician.spikeball.game.SpikeBallGame;
import Mathematician.spikeball.game.SpikeBallGameHandler;
import Mathematician.spikeball.gamemechanics.powerups.Freeze;
import Mathematician.spikeball.gamemechanics.powerups.PowerUp;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

public class HitSpikeBallEvent implements Listener {

    @EventHandler
    public void playerPunchEvent(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Slime){
            Player player = (Player) event.getDamager();
            event.setCancelled(playerHitSpikeBall(player));
        }
    }

    @EventHandler
    public void playerRightClickSlimeEvent(PlayerInteractEntityEvent event){
        if(event.getRightClicked() instanceof Slime){
            Player player = event.getPlayer();
            event.setCancelled(playerHitSpikeBall(player));
        }
    }

    public boolean playerHitSpikeBall(Player player){
        SpikeBallGame spikeBallGame = SpikeBallGameHandler.getGamePlayerIsIn(player);
        if(spikeBallGame != null){
            if(spikeBallGame.getHitType(player) != null){
                SpikeBallGame.HitType hitType = spikeBallGame.getHitType(player);
                Vector playerDirection = player.getLocation().getDirection();

                switch (hitType){
                    case SPIKING:
                        spikeBallGame.addVelocityToSpikeBall(playerDirection.normalize());
                        break;

                    case UP:
                        spikeBallGame.changeSpikeBallVelocity(new Vector(spikeBallGame.getSpikeBall().getEntity().getVelocity().getX()/2.5,0.75,spikeBallGame.getSpikeBall().getEntity().getVelocity().getZ()/2.5));
                        break;
                }
                spikeBallGame.addHit();
                if(spikeBallGame.getHitCount() > 3){
                    spikeBallGame.updateScore();
                }
                if(spikeBallGame.isIfPowerUpGoingOn()){
                    PowerUp powerUp = spikeBallGame.getLastUsedPowerUp();
                    if(powerUp instanceof Freeze){
                        powerUp.reset();
                    }
                }
            }
            return true;
        }
        return false;
    }

}
