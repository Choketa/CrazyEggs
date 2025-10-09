package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.utils.ColorUtils;
import me.choketa.crazyeggs.utils.EggUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class PluginEgg {
    private String name;
    protected String displayName;
    private ItemStack eggItem;
    private Listener listener;
    private ShapedRecipe recipe;
    private File file;
    private YamlConfiguration customFile;
    public PluginEgg(String name) {
        this.name = name;
        boolean isNew = false;
        this.file = new File(getPlugin().getDataFolder()+"/eggs", name+".yml");
        if (!file.exists()) {
            try {
                isNew = file.createNewFile();
            } catch (IOException e) {
                getPlugin().getLogger().warning("Unable to create file for "+name);
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.options().copyDefaults(true);
        this.displayName = isNew ? "&#b32222&l"+name.replace('_', ' ') : get("display-name");
        if (isNew)
         setDefaults();
        save();
    }
    public FileConfiguration get() {
        return customFile;
    }
    public <T> T get(String path) {
        return (T) customFile.get(path);
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
    public void setDefaults() {
        set("display-name",
                displayName,
                Collections.singletonList("The name of the egg item."));

        set("lore",
                List.of("&cThis is the default egg lore of the egg item."),
                List.of("The lore of the egg item. Keep the dash only if you want no lore."));

        set("is-craftable",
                true,
                Collections.singletonList("Determines whether the egg is craftable or not."));

        set("damage",
                15,
                Collections.singletonList("The damage inflicted upon an entity when hit"));

        set("enable-spawn-eggs-drop",
                true,
                Collections.singletonList("Determines if a living entity will drop their corresponding spawn egg"));

        set("set-glint",
                true,
                Collections.singletonList("Determines whether the egg is decorated with an enchantment glint"));

        set("custom-model-data", -1, Collections.singletonList("For resourcepacks"));
    }
    public ItemStack getEggItem() {
        if (eggItem == null) {
            this.eggItem = new ItemStack(Material.EGG);
            eggItem.editMeta(Damageable.class, meta -> {
                meta.itemName(ColorUtils.format(get("display-name")));
                List<String> lore = get("lore");
                if (lore != null && !lore.isEmpty())
                 meta.lore(EggUtils.adaptLore(lore));
                meta.setDamage(get("damage"));
                if (get("set-glint")) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                int customModelData = get("custom-model-data");
                if (customModelData != -1)
                    meta.setCustomModelData(customModelData);
                meta.getPersistentDataContainer().set(new NamespacedKey(getPlugin(), "crazyeggs" + name), PersistentDataType.BOOLEAN, true);
            });
        }
        return eggItem.clone();
    }
    public void set(@NotNull String path, Object obj, @Nullable List<String> description) {
        customFile.set(path, obj);
        if (description != null)
            customFile.setComments(path, description);
    }
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}