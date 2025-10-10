package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.eggs.eggs.PluginEgg;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

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
        Map<Character, String> characterMat = egg.get("materials");
        List<String> shape = egg.get("recipe");
        recipe.shape(shape.get(0), shape.get(1), shape.get(2));
        characterMat.forEach((character, value) -> {
            Material mat = Material.matchMaterial(value);
            if (mat != null)
             recipe.setIngredient(character, mat);
            else
                recipe.setIngredient(character, EggManager.getEggManager().getEggByName(value).getEggItem());
        });
        plugin.getRecipes().add(egg.getKey());
        plugin.getServer().addRecipe(recipe);
    }

}
