package Mathematician.spikeball.gamemechanics.cooldown;

import java.util.function.Consumer;

public class Cooldown {

    private long finishTime;
    private String tag;
    private Consumer endActivationFunction;

    public Cooldown (String tag, long coolDownTime){
        this.tag = tag;
        this.finishTime = System.currentTimeMillis() + coolDownTime;
        endActivationFunction = b -> getTag();
    }

    public Cooldown(String tag, long coolDownTime, Consumer endActivationFunction){
        this.tag = tag;
        this.finishTime = System.currentTimeMillis() + coolDownTime;
        this.endActivationFunction = endActivationFunction;
    }

    public double getTimeRemainingInSeconds(){
        return (finishTime - System.currentTimeMillis()) / 1000.0;
    }

    public boolean isCompleted(){
        return finishTime - System.currentTimeMillis() <= 0;
    }

    public String getTag(){
        return tag;
    }

    public void activateFunction(Object o){
        endActivationFunction.accept(o);
    }
}
