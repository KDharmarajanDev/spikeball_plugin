package Mathematician.spikeball.gameelements;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Scaffolding;

public class SpikeBallNet {

    private Block spikeBallNet;

    public SpikeBallNet (Block net){
        if(net.getType() == Material.SCAFFOLDING){
            spikeBallNet = net;
        }
    }

    public Location getSpikeBallNetLocation(){
        return spikeBallNet.getLocation();
    }

}
