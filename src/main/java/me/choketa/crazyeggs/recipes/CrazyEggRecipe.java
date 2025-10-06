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

    public CrazyEggRecipe() {
        this.plugin = getPlugin();
    }
    private static final ItemStack CRAZY_EGG;
    static {
        CRAZY_EGG = new ItemStack(Material.EGG, getPlugin().getConfig().getInt("upon-craft-amount"));
        ItemMeta meta = CRAZY_EGG.getItemMeta();

        setCrazyName(getPlugin(), CRAZY_EGG, meta);
        setCrazyLore(getPlugin(), CRAZY_EGG, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(getPlugin(),"crazyegg"), PersistentDataType.INTEGER,69);
        int customModelData = getPlugin().getConfig().getInt("crazy-custom-model-data");
        if (customModelData != -1)
            meta.setCustomModelData(customModelData);
        CRAZY_EGG.setItemMeta(meta);
    }

    public ItemStack eggItem() {
      return CRAZY_EGG;
    }

    public void eggCraft() {
        // Not ideal customization, but for now this approach is good enough.
        if (!plugin.getConfig().getBoolean("is-craftable")) return;
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"egg"), eggItem());
        List<String> crazyEggRecipe = plugin.getConfig().getStringList("crazy-egg-recipe");
        List<String> materials = plugin.getConfig().getStringList("materials");

        recipe.shape(crazyEggRecipe.get(0), crazyEggRecipe.get(1), crazyEggRecipe.get(2));
        for (String material : materials)
            recipe.setIngredient(material.charAt(0), Material.matchMaterial(material));

        Bukkit.addRecipe(recipe);
    }
}
