package me.choketa.crazyeggs.eggs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public abstract class AbstractEgg {
    String displayName;
    private ItemStack eggItem;
    private Listener listener;
    private ShapedRecipe recipe;
    private File file;
    private FileConfiguration customFile;
    public AbstractEgg(String name) {
        this.file = new File(getPlugin().getDataFolder(), name+".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                //
            }
        }
        this.displayName = name;
        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.options().copyDefaults(true);
        save();
    }
    public FileConfiguration get() {
        return customFile;
    }
    public FileConfiguration save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customFile;
    }
    public FileConfiguration reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
        return customFile;
    }
    public abstract void setInfo();
    public void addDefault(@NotNull String path, Object obj, @Nullable List<String> description) {
        customFile.set(path, obj);
        if (description != null)
            customFile.setComments(path, description);
    }
    public void addAttribute(@NotNull String path, Object obj, @Nullable List<String> description) {
        customFile.set(path, obj);
        customFile.setComments(path, description);
    }
}