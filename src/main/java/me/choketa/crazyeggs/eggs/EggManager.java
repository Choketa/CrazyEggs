package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EggManager {
    private static EggManager eggManager;
    private final CrazyEggs plugin;
    private final List<PluginEgg> eggs;

    private EggManager() {
        this.plugin = CrazyEggs.getPlugin();
        this.eggs = new ArrayList<>();
        File eggsFolder = new File(plugin.getDataFolder()+"/eggs");
        try {
            eggsFolder.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File[] files = new File(eggsFolder.getPath()).listFiles();

        for (File file : files)
            eggs.add((new PluginEgg(file.getName().replace(".yml", ""))));

        plugin.getServer().getPluginManager().registerEvents(new EggListener(), plugin);
    }
    public static EggManager getEggManager() {
        if (eggManager == null)
           eggManager = new EggManager();
        return eggManager;
    }
    public List<PluginEgg> getEggs() {
        return eggs;
    }
}