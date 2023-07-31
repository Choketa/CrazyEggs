package me.choketa.crazyeggs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static me.choketa.crazyeggs.utils.EggUtils.*;


public class EggRecipe {
    CrazyEggs plugin;

    public EggRecipe(CrazyEggs plugin) {
        this.plugin = plugin;
    }


    public ItemStack eggItem() {
        ItemStack item = new ItemStack(Material.EGG, plugin.getConfig().getInt("upon-craft-amount"));
        ItemMeta meta = item.getItemMeta();


        setName(plugin, item, meta);
        setLore(plugin, item, meta);
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin,"crazyegg"), PersistentDataType.INTEGER,69);
        item.setItemMeta(meta);

        return item;
    }

    public void eggCraft() {
        // Not ideal customization, but for now that's good enough.
        if (!plugin.getConfig().getBoolean("is-craftable")) return;
        eggItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"egg"), eggItem());
        List<String> crazy_egg_recipe = plugin.getConfig().getStringList("crazy-egg-recipe");
        List<String> materials = plugin.getConfig().getStringList("materials");
        recipe.shape(crazy_egg_recipe.get(0), crazy_egg_recipe.get(1), crazy_egg_recipe.get(2));
        for (String material : materials) {
            recipe.setIngredient(material.charAt(0), Material.matchMaterial(material));
        }
        Bukkit.addRecipe(recipe);
    }
}
