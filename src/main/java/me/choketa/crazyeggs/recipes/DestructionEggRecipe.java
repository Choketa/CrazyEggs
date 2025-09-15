package me.choketa.crazyeggs.recipes;

import me.choketa.crazyeggs.CrazyEggs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;
import static me.choketa.crazyeggs.utils.EggUtils.*;

public class DestructionEggRecipe {
    private final CrazyEggs plugin;
    private final CrazyEggRecipe crazyEgg;

    public static final ItemStack DESTRUCTION_EGG_ITEM;
    static {
        DESTRUCTION_EGG_ITEM = new ItemStack(Material.EGG, getPlugin().getConfig().getInt("upon-destruction-craft-amount"));
        ItemMeta meta = DESTRUCTION_EGG_ITEM.getItemMeta();


        setDestructionName(getPlugin(), DESTRUCTION_EGG_ITEM, meta);
        setDestructionLore(getPlugin(), DESTRUCTION_EGG_ITEM, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(getPlugin(),"destructionegg"), PersistentDataType.INTEGER,69);
        DESTRUCTION_EGG_ITEM.setItemMeta(meta);
    }

    public DestructionEggRecipe(CrazyEggs plugin) {
        this.plugin = plugin;
        crazyEgg = new CrazyEggRecipe(plugin);
    }


    public ItemStack eggItem() {
        ItemStack item = new ItemStack(Material.EGG, plugin.getConfig().getInt("upon-destruction-craft-amount"));
        ItemMeta meta = item.getItemMeta();


        setDestructionName(plugin, item, meta);
        setDestructionLore(plugin, item, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin,"destructionegg"), PersistentDataType.INTEGER,69);
        item.setItemMeta(meta);

        return item;
    }

    public void eggCraft() {
        // Not ideal customization, but for now that's good enough.
        if (!plugin.getConfig().getBoolean("is-destruction-craftable")) return;
        eggItem();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"destructionegg"), eggItem());

        recipe.shape("EEE","ECE","EEE");
        recipe.setIngredient('E', new RecipeChoice.ExactChoice(crazyEgg.eggItem()));
        recipe.setIngredient('C', Material.CREEPER_SPAWN_EGG);
        Bukkit.addRecipe(recipe);
    }
}
