package Mathematician.spikeball.gameelements;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class SpikeBallNet {

    public static boolean ifUsingTexturePack;
    private Block spikeBallNet;
    private ArmorStand spikeBallNetHolder;

    public SpikeBallNet (Block block){
        if (block.getType() == Material.SCAFFOLDING) {
            spikeBallNet = block;
        }
    }

    public SpikeBallNet (ArmorStand spikeBallNetHolder){
        if(spikeBallNetHolder != null) {
            this.spikeBallNetHolder = spikeBallNetHolder;
        }
    }

    public Location getSpikeBallNetLocation(){
        if(!ifUsingTexturePack){
            return spikeBallNet.getLocation();
        }
        return spikeBallNetHolder.getLocation();
    }

    public Block toBlock(){
        if(!ifUsingTexturePack){
            return spikeBallNet;
        }
        return null;
    }

    public ArmorStand getSpikeBallNetHolder(){
        if(ifUsingTexturePack){
            return spikeBallNetHolder;
        }
        return null;
    }

    public boolean equals(SpikeBallNet net){
        return net.getSpikeBallNetLocation().equals(getSpikeBallNetLocation());
    }

    public BoundingBox getSpikeBallDetectionBox(){
        BoundingBox boundingBox;
        if(!ifUsingTexturePack){
            boundingBox = spikeBallNet.getBoundingBox().shift(0, 0.6, 0).expand(new Vector(-0.05,-0.4,-0.05));
        } else {
            boundingBox = spikeBallNetHolder.getBoundingBox().shift(0,-0.3,0).expand(0.3,-0.7,0.3);
        }
        return boundingBox;
    }
}
