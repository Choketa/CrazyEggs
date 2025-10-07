package me.choketa.crazyeggs;

import me.choketa.crazyeggs.commands.GiveDestructionEggCommand;
import me.choketa.crazyeggs.listeners.CrazyEggEvents;
import me.choketa.crazyeggs.commands.GiveEggCommand;
import me.choketa.crazyeggs.listeners.DestructionEggEvents;
import me.choketa.crazyeggs.listeners.OnOpJoinEvent;
import me.choketa.crazyeggs.recipes.CrazyEggRecipe;
import me.choketa.crazyeggs.recipes.DestructionEggRecipe;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public final class CrazyEggs extends JavaPlugin {
    private CrazyEggRecipe egg;
    private DestructionEggRecipe degg;
    private static CrazyEggs plugin;
    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        egg = new CrazyEggRecipe();
        egg.eggCraft();
        degg = new DestructionEggRecipe();
        degg.eggCraft();

        getServer().getPluginManager().registerEvents(new CrazyEggEvents(), this);
        getServer().getPluginManager().registerEvents(new DestructionEggEvents(), this);
        getServer().getPluginManager().registerEvents(new OnOpJoinEvent(), this);

        GiveDestructionEggCommand destruction = new GiveDestructionEggCommand();
        PluginCommand destructionCommand = getCommand("getdestructionegg");

        destructionCommand.setExecutor(destruction);
        destructionCommand.setTabCompleter(destruction);

        GiveEggCommand egg = new GiveEggCommand();
        PluginCommand eggCommand = getCommand("getegg");

        eggCommand.setExecutor(egg);
        eggCommand.setTabCompleter(egg);


        new UpdateChecker().getVersion(version -> {
            String curr = "\""+getPlugin().getDescription().getVersion()+"\"";
            if (version.replaceFirst("\"[0-9]\\.[0-9]\\.[0-9]\"", curr).equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().warning("There is a new update available!");
                getLogger().warning("Go to https://modrinth.com/plugin/crazy-eggs in order to update!");
            }
        });
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