package Mathematician.spikeball.commands.subcommands;

public enum SubCommandKeys {

    PLACE("place", PlaceSubCommand.class),
    LEAVE("leave", LeaveSubCommand.class),
    START("start", StartSubCommand.class),
    RELOAD("reload", ReloadSubCommand.class),
    VISUALIZE("visualize", VisualizeSubCommand.class);

    private String value;
    private Class<? extends SubCommand> subCommandClass;

    SubCommandKeys(String value, Class<? extends SubCommand> channelPacketClass){
        this.value = value;
        this.subCommandClass = channelPacketClass;
    }

    public String getValue(){
        return value;
    }

    public Class<? extends SubCommand> getSubCommandClass(){
        return subCommandClass;
    }

}
