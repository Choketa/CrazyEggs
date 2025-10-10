package me.choketa.crazyeggs.eggs.eggs;

import java.util.List;
import java.util.Map;

public class DestructionEgg extends PluginEgg {
    public DestructionEgg() {
        super("Destruction_Egg");
        if (isOld) return;
        set("should-explode", true);
        set("display-name", "&5&lDestruction Egg");
        set("lore", List.of("&4Dangerous weapon. Use foolishly."));
        set("recipe",
                List.of("EEE", "ECE", "EEE"));
        set("materials",
                Map.of('E', "Crazy_Egg", 'C', "CREEPER_SPAWN_EGG"));
        save();
    }
}
