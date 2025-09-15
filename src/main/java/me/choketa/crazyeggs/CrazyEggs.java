package me.choketa.crazyeggs;

import me.choketa.crazyeggs.commands.GiveDestructionEggCommand;
import me.choketa.crazyeggs.eggs.CrazyEgg;
import me.choketa.crazyeggs.listeners.CrazyEggEvents;
import me.choketa.crazyeggs.commands.GiveEggCommand;
import me.choketa.crazyeggs.listeners.DestructionEggEvents;
import me.choketa.crazyeggs.listeners.OnOpJoinEvent;
import me.choketa.crazyeggs.recipes.CrazyEggRecipe;
import me.choketa.crazyeggs.recipes.DestructionEggRecipe;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

public final class CrazyEggs extends JavaPlugin {

    private static CrazyEggs plugin;
    private UpdateChecker checker;
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        CrazyEggRecipe egg = new CrazyEggRecipe(this);
        egg.eggCraft();
        CrazyEgg crazyEgg = new CrazyEgg("Crassius Curio");
        crazyEgg.setInfo();
        DestructionEggRecipe degg = new DestructionEggRecipe(this);
        degg.eggCraft();

        getServer().getPluginManager().registerEvents(new CrazyEggEvents(this), this);
        getServer().getPluginManager().registerEvents(new DestructionEggEvents(this), this);
        getServer().getPluginManager().registerEvents(new OnOpJoinEvent(this), this);

        getCommand("getdestructionegg").setExecutor(new GiveDestructionEggCommand(this));
        getCommand("getegg").setExecutor(new GiveEggCommand(this));

        checker = new UpdateChecker(this, 111676);
        checker.getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().warning("There is a new update available!");
                getLogger().warning("Go to https://www.spigotmc.org/resources/1-20-crazyeggs.111676/ in order to update!");
            }
        });
    }
    @Override
    public void onDisable() {
        checker.onDisable();
    }
    public static CrazyEggs getPlugin() {
        return plugin;
    }
}

