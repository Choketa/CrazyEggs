package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class EggManager {
    private static EggManager eggManager;
    private final CrazyEggs plugin;
    private final List<PluginEgg> eggs;
    private final File eggsFolder;

    private EggManager() {
        this.plugin = CrazyEggs.getPlugin();
        this.eggs = new ArrayList<>();
        eggsFolder = new File(plugin.getDataFolder()+"/eggs");
        try {
            eggsFolder.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadEggs() {
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
    public PluginEgg getEggByName(String name) {
        for (PluginEgg egg : eggs)
            if (egg.getName().equalsIgnoreCase(name)) return egg;
        return null;
    }
    public List<PluginEgg> getEggs() {
        return eggs;
    }

    public PluginEgg getEggByPDC(Projectile entity) {
        if (!(entity instanceof Egg egg)) return null;
        ItemStack item = egg.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (PluginEgg pluginEgg : eggs)
            if (pdc.has(pluginEgg.getKey())) return pluginEgg;
        return null;
    }
    public PluginEgg getEggByPDC(ItemStack item) {
        if (item.getType() != Material.EGG) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (PluginEgg pluginEgg : eggs)
            if (pdc.has(pluginEgg.getKey())) return pluginEgg;
        return null;
    }
}