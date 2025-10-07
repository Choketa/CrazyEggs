package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;

import java.util.ArrayList;
import java.util.List;

public class EggManager {
    private final CrazyEggs plugin;
    private final List<AbstractEgg> eggs;

    public EggManager() {
        this.plugin = CrazyEggs.getPlugin();
        this.eggs = new ArrayList<>();
    }
}