package me.choketa.crazyeggs.recipes;

import me.choketa.crazyeggs.CrazyEggs;
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

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;
import static me.choketa.crazyeggs.utils.EggUtils.*;


public class CrazyEggRecipe {
    private final CrazyEggs plugin;

    public CrazyEggRecipe(CrazyEggs plugin) {
        this.plugin = plugin;
    }


    public static final ItemStack CRAZY_EGG_ITEM;
    static {
        CRAZY_EGG_ITEM = new ItemStack(Material.EGG, getPlugin().getConfig().getInt("upon-craft-amount"));
        ItemMeta meta = CRAZY_EGG_ITEM.getItemMeta();


        setCrazyName(getPlugin(), CRAZY_EGG_ITEM, meta);
        setCrazyLore(getPlugin(), CRAZY_EGG_ITEM, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(getPlugin(),"crazyegg"), PersistentDataType.INTEGER,69);
        CRAZY_EGG_ITEM.setItemMeta(meta);

    }

    public ItemStack eggItem() {
        ItemStack item = new ItemStack(Material.EGG, plugin.getConfig().getInt("upon-craft-amount"));
        ItemMeta meta = item.getItemMeta();


        setCrazyName(plugin, item, meta);
        setCrazyLore(plugin, item, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
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
        List<String> crazyEggRecipe = plugin.getConfig().getStringList("crazy-egg-recipe");
        List<String> materials = plugin.getConfig().getStringList("materials");

        recipe.shape(crazyEggRecipe.get(0), crazyEggRecipe.get(1), crazyEggRecipe.get(2));
        for (String material : materials) {
            recipe.setIngredient(material.charAt(0), Material.matchMaterial(material));
        }
        Bukkit.addRecipe(recipe);
    }
}
