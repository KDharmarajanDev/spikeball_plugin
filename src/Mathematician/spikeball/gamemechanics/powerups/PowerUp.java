package Mathematician.spikeball.gamemechanics.powerups;

import Mathematician.spikeball.gamemechanics.SpikeBallGame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PowerUp {

    boolean applyEffect();

    String getTag();

    Player getPlayer();

    ItemStack getItemStack();

    void update();

    boolean isCompleted();

    void reset();

    SpikeBallGame getSpikeBallGame();
}
