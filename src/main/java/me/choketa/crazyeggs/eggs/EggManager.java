package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.eggs.eggs.PluginEgg;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EggManager {
    private static EggManager eggManager;
    private final CrazyEggs plugin;
    private final List<PluginEgg> eggs;
    private final File eggsFolder;
    private final boolean isOld;

    private EggManager() {
        this.plugin = CrazyEggs.getPlugin();
        this.eggs = new ArrayList<>();
        this.eggsFolder = new File(plugin.getDataFolder() + "/eggs");
        this.isOld = eggsFolder.exists();

        if (this.isOld) return;
        eggsFolder.mkdirs();
    }

    public void makeCrazy(PluginEgg egg) {
        egg.set("display-name", "&c&lCrazy &#b32222&lEgg");
        egg.set("lore", List.of("&cKill with the crazy egg!"));
        egg.set("cooldown", 0);
        egg.save();
    }

    public void makeDestruction(PluginEgg egg) {
        egg.set("should-explode", true);
        egg.set("display-name", "&5&lDestruction Egg");
        egg.set("lore", List.of("&4Dangerous weapon. Use foolishly."));
        egg.set("recipe",
                List.of("EEE", "ETE", "EEE"));
        egg.set("materials.E", "Crazy_Egg");
        egg.set("materials.T", "CREEPER_SPAWN_EGG");
        egg.save();
    }

    public void loadEggs() {
        if (!this.isOld) {
            PluginEgg crazy = new PluginEgg("Crazy_Egg");
            makeCrazy(crazy);
            PluginEgg destruction = new PluginEgg("Destruction_Egg");
            makeDestruction(destruction);
            eggs.add(crazy);
            eggs.add(destruction);
        }
        List<File> files;
        if (!isOld) files = Arrays.stream(eggsFolder.listFiles()).filter(f ->
               f.getName().equals("Crazy_Egg") || f.getName().equals("Destruction_Egg")).toList();
        else files = Arrays.stream(eggsFolder.listFiles()).toList();
        for (File file : files) {
            PluginEgg egg = new PluginEgg(file.getName().replace(".yml", ""));
            eggs.add(egg);
        }
        for (PluginEgg egg : eggs)
            egg.initializeRecipe();
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