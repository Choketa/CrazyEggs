package me.choketa.crazyeggs;

import me.choketa.crazyeggs.commands.GiveDestructionEggCommand;
import me.choketa.crazyeggs.listeners.CrazyEggEvents;
import me.choketa.crazyeggs.commands.GiveEggCommand;
import me.choketa.crazyeggs.listeners.DestructionEggEvents;
import me.choketa.crazyeggs.listeners.OnOpJoinEvent;
import me.choketa.crazyeggs.recipes.CrazyEggRecipe;
import me.choketa.crazyeggs.recipes.DestructionEggRecipe;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrazyEggs extends JavaPlugin {
    private CrazyEggRecipe egg;
    private DestructionEggRecipe degg;
    private static CrazyEggs plugin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        egg = new CrazyEggRecipe();
        egg.eggCraft();
        degg = new DestructionEggRecipe();
        degg.eggCraft();

        getServer().getPluginManager().registerEvents(new CrazyEggEvents(), this);
        getServer().getPluginManager().registerEvents(new DestructionEggEvents(), this);
        getServer().getPluginManager().registerEvents(new OnOpJoinEvent(), this);

        getCommand("getdestructionegg").setExecutor(new GiveDestructionEggCommand());
        getCommand("getegg").setExecutor(new GiveEggCommand());

        new UpdateChecker(111676).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().warning("There is a new update available!");
                getLogger().warning("Go to https://www.spigotmc.org/resources/1-20-crazyeggs.111676/ in order to update!");
            }
        });
        plugin = this;
    }
    public static CrazyEggs getPlugin() {
        return plugin;
    }
    public CrazyEggRecipe getCrazyEggsRecipe() {
        return egg;
    }

    public DestructionEggRecipe getDestructionEggRecipe() {
        return degg;
    }
}

