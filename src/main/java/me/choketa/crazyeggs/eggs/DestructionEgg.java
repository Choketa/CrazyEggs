package me.choketa.crazyeggs.eggs;

public class DestructionEgg extends PluginEgg{
    public DestructionEgg() {
        super("Destruction_Egg");
        set("should-explode", true);
        set("display-name", "&5&lDestruction Egg");
        save();
    }
}
