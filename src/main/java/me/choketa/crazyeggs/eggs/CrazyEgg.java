package me.choketa.crazyeggs.eggs;

import java.util.List;

public class CrazyEgg extends AbstractEgg{
    public CrazyEgg(String name) {
        super(name);
    }

    @Override
    public void setInfo() {
        //FileConfiguration customFile = get();
        addDefault("display-name",
                displayName,
                List.of("The name of the egg item."));

        addDefault("lore",
                List.of("&cKill with the crazy egg!"),
                List.of("The lore of the egg item. Keep the dash only if you want no lore.)"));

        addDefault("is-craftable",
                true,
                List.of("Determines whether the egg is craftable or not."));

        addDefault("damage",
                15,
                List.of("The damage inflicted upon an entity when hit"));

    }
}