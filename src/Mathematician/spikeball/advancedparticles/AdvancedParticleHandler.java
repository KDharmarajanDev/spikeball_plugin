package Mathematician.spikeball.advancedparticles;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ListIterator;

public class AdvancedParticleHandler {

    private static ArrayList<AdvancedParticleGenerator> advancedParticleGenerators = new ArrayList<>();

    public static void update(){
        for(AdvancedParticleGenerator generator : advancedParticleGenerators){
            generator.update();
        }
    }

    public static void addAdvancedParticleGenerator(AdvancedParticleGenerator generator){
        advancedParticleGenerators.add(generator);
    }

    public static void removeAdvancedParticleGenerator(Player player){
        ListIterator iterator = advancedParticleGenerators.listIterator();
        while(iterator.hasNext()) {
            AdvancedParticleGenerator generator = (AdvancedParticleGenerator) iterator.next();
            if (generator.getFollowEntity() instanceof Player) {
                Player p = (Player) generator.getFollowEntity();
                if (p.equals(player)) {
                    iterator.remove();
                }
            }
        }
    }
}
