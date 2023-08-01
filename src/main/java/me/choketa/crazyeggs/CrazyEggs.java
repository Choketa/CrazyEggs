package me.choketa.crazyeggs;

import me.choketa.crazyeggs.listeners.EggEvents;
import me.choketa.crazyeggs.commands.GiveEggCommand;
import me.choketa.crazyeggs.listeners.OnOpJoinEvent;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class CrazyEggs extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        EggRecipe egg = new EggRecipe(this);
        egg.eggCraft();

        getServer().getPluginManager().registerEvents(new EggEvents(this), this);
        getServer().getPluginManager().registerEvents(new OnOpJoinEvent(this), this);


        getCommand("getegg").setExecutor(new GiveEggCommand(this));

        new UpdateChecker(this, 111676).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().warning("There is a new update available!");
                getLogger().warning("Go to https://www.spigotmc.org/resources/1-20-crazyeggs.111676/ in order to update!");
            }
        });

    }
}

