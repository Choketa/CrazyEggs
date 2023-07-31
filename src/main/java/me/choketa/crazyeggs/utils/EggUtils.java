package me.choketa.crazyeggs.utils;


import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class EggUtils {

    public static boolean isCrazyEgg(JavaPlugin plugin, ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "crazyegg"), PersistentDataType.INTEGER);
    }
    public static void setLore(JavaPlugin plugin, ItemStack item, ItemMeta meta) {
        List<String> lore = plugin.getConfig().getStringList("crazy-egg-lore");
        lore.replaceAll(ColorUtils::format);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    public static void setName(JavaPlugin plugin, ItemStack item, ItemMeta meta) {
        String name = plugin.getConfig().getString("crazy-egg-name");
        meta.setDisplayName(format(name));
        item.setItemMeta(meta);
    }
}
