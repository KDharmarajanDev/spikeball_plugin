package Mathematician.spikeball.advancedparticles;

import com.mojang.datafixers.types.Func;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AdvancedParticleGenerator {

    private Entity followEntity;
    private Function<Double, Vector> parametricEquation;

    private double lowerTBound;
    private double upperTBound;
    private double currentTValue;

    private int numTIncreasePerUpdate;
    private double tIncreaseIncrement;

    private Particle particleType;

    private boolean if2dProjection;


    public AdvancedParticleGenerator(Entity followEntity, Function<Double, Vector> parametricEquation, double lowerTBound, double upperTBound, int numTIncreasePerUpdate, double tIncreaseIncrement, Particle spawnParticle, boolean if2dProjection) {
        this.followEntity = followEntity;
        this.parametricEquation = parametricEquation;
        this.lowerTBound = lowerTBound;
        this.upperTBound = upperTBound;
        this.numTIncreasePerUpdate = numTIncreasePerUpdate;
        this.tIncreaseIncrement = tIncreaseIncrement;
        particleType = spawnParticle;
        currentTValue = lowerTBound;
        this.if2dProjection = if2dProjection;
    }

    public Entity getFollowEntity(){
        return followEntity;
    }

    public void update(){
        if(followEntity != null){
            Location entityLocation = followEntity.getLocation();
            for(int i = 0; i < numTIncreasePerUpdate; i++){
                Vector outputVector = parametricEquation.apply(currentTValue);
                if(if2dProjection){
                    //The following three vectors are the basis vectors of the plane
                    Vector normalVectorToPlane = entityLocation.getDirection().normalize();
                    Vector yAxis = perp(normalVectorToPlane, new Vector(0, 1, 0)).normalize();
                    Vector xAxis = yAxis.getCrossProduct(normalVectorToPlane).normalize();
                    Vector transformedOutputVector = new Vector(outputVector.getX() * xAxis.getX() + outputVector.getY() * yAxis.getX(),
                            outputVector.getX() * xAxis.getY() + outputVector.getY() * yAxis.getY(),outputVector.getX() * xAxis.getZ() + outputVector.getY() * yAxis.getZ());
                    if(followEntity instanceof Player){
                        followEntity.getWorld().spawnParticle(particleType, new Location(followEntity.getWorld(),entityLocation.getX() + transformedOutputVector.getX(),entityLocation.getY() + transformedOutputVector.getY() + 1, entityLocation.getZ() + transformedOutputVector.getZ()),1);
                    } else {
                        followEntity.getWorld().spawnParticle(particleType, new Location(followEntity.getWorld(),entityLocation.getX() + transformedOutputVector.getX(),entityLocation.getY() + transformedOutputVector.getY(), entityLocation.getZ() + transformedOutputVector.getZ()),1);
                    }
                } else {
                    followEntity.getWorld().spawnParticle(particleType, entityLocation.add(outputVector),1);
                }
                if(currentTValue + tIncreaseIncrement > upperTBound) {
                    currentTValue = lowerTBound;
                } else if(currentTValue + tIncreaseIncrement < lowerTBound){
                    currentTValue = upperTBound;
                } else {
                    currentTValue += tIncreaseIncrement;
                }
            }
        }
    }

    public Vector perp(Vector onto, Vector u) {
        return u.clone().subtract(proj(onto, u));
    }

    public Vector proj(Vector onto, Vector u) {
        return onto.clone().multiply(onto.dot(u) / onto.lengthSquared());
    }

}
