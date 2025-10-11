package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.eggs.eggs.PluginEgg;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EggRecipe {
    public EggRecipe(PluginEgg egg) {
        CrazyEggs plugin = CrazyEggs.getPlugin();
        NamespacedKey key = egg.getKey();
        if (plugin.getRecipes().contains(key)) return;
        ItemStack item = egg.getEggItem();
        item.setAmount(egg.get("upon-craft-amount"));
        ShapedRecipe recipe = new ShapedRecipe(egg.getKey(), item);
        Map<Character, String> characterMat = new HashMap<>();
        ConfigurationSection section = egg.getConfig().getConfigurationSection("materials");
        for (String str : section.getKeys(false))
            characterMat.put(str.charAt(0), section.getString(str));

        List<String> shape = egg.get("recipe");
        recipe.shape(shape.get(0), shape.get(1), shape.get(2));
        characterMat.forEach((character, value) -> {
            Material mat = Material.matchMaterial(value);
            if (mat != null)
                recipe.setIngredient(character, mat);
            else {
                try {
                    ItemStack pluginEgg = EggManager.getEggManager().getEggByName(value).getEggItem();
                    recipe.setIngredient(character, new RecipeChoice.ExactChoice(pluginEgg));
                } catch (NullPointerException e) {
                    plugin.getLogger().warning("Unable to create recipe for "+egg.getName()+": Unable to find egg "+value);
                }
            }
        });
        plugin.getRecipes().add(egg.getKey());
        plugin.getServer().addRecipe(recipe);
    }

}
