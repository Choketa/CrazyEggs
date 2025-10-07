package me.choketa.crazyeggs.utils;


import org.bukkit.NamespacedKey;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;
import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class EggUtils {

    public static boolean isCrazyEgg(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(getPlugin(), "crazyegg"), PersistentDataType.INTEGER);
    }
    public static boolean isCrazyEgg(Entity entity) {
        if (!(entity instanceof Egg)) return false;
        ItemStack item = ((Egg) entity).getItem();
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(getPlugin(), "crazyegg"), PersistentDataType.INTEGER);
    }
    public static void setCrazyLore(ItemStack item, ItemMeta meta) {
        List<String> lore = getPlugin().getConfig().getStringList("crazy-egg-lore");
        lore.replaceAll(ColorUtils::format);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    public static void setCrazyName(ItemStack item, ItemMeta meta) {
        String name = getPlugin().getConfig().getString("crazy-egg-name");
        meta.setDisplayName(format(name));
        item.setItemMeta(meta);
    }
    public static boolean isDestructionEgg(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(getPlugin(), "destructionegg"), PersistentDataType.INTEGER);
    }
    public static boolean isDestructionEgg(Entity entity) {
        if (!(entity instanceof Egg)) return false;
        ItemStack item = ((Egg) entity).getItem();
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(getPlugin(), "destructionegg"), PersistentDataType.INTEGER);
    }
    public static void setDestructionLore(ItemStack item, ItemMeta meta) {
        List<String> lore = getPlugin().getConfig().getStringList("destruction-egg-lore");
        lore.replaceAll(ColorUtils::format);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    public static void setDestructionName(ItemStack item, ItemMeta meta) {
        String name = getPlugin().getConfig().getString("destruction-egg-name");
        meta.setDisplayName(format(name));
        item.setItemMeta(meta);
    }
}

