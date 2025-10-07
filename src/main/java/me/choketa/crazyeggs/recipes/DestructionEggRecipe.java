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
    private static final ItemStack DESTRUCTION_EGG;

    public DestructionEggRecipe() {
        this.plugin = getPlugin();
        crazyEgg = plugin.getCrazyEggsRecipe();
    }
    static {
        DESTRUCTION_EGG = new ItemStack(Material.EGG, getPlugin().getConfig().getInt("upon-destruction-craft-amount"));
        ItemMeta meta = DESTRUCTION_EGG.getItemMeta();

        setDestructionName(DESTRUCTION_EGG, meta);
        setDestructionLore(DESTRUCTION_EGG, meta);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(new NamespacedKey(getPlugin(),"destructionegg"), PersistentDataType.INTEGER,69);
        int customModelData = getPlugin().getConfig().getInt("destruction-custom-model-data");
        if (customModelData != -1)
            meta.setCustomModelData(customModelData);
        DESTRUCTION_EGG.setItemMeta(meta);
    }

    public ItemStack eggItem() {
        return DESTRUCTION_EGG;
    }

    public void eggCraft() {
        // Not ideal customization, but for now this approach is good enough.
        if (!plugin.getConfig().getBoolean("is-destruction-craftable")) return;
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"destructionegg"), eggItem());

        recipe.shape("EEE","ECE","EEE");
        recipe.setIngredient('E', new RecipeChoice.ExactChoice(crazyEgg.eggItem()));
        recipe.setIngredient('C', Material.CREEPER_SPAWN_EGG);
        Bukkit.addRecipe(recipe);
    }
}
