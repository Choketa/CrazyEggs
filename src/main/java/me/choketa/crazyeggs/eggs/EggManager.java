package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import org.bukkit.entity.Egg;

import java.util.ArrayList;
import java.util.List;

public class EggManager {
    private final CrazyEggs plugin;
    private final List<AbstractEgg> eggs;

    public EggManager() {
        this.plugin = CrazyEggs.getPlugin();
        this.eggs = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(new EggListener(), plugin);
    }
}