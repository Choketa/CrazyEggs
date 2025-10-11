package me.choketa.crazyeggs;

import me.choketa.crazyeggs.commands.GetEgg;
import me.choketa.crazyeggs.eggs.EggManager;
import me.choketa.crazyeggs.listeners.OnOpJoinEvent;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public final class CrazyEggs extends JavaPlugin {
    private static CrazyEggs plugin;
    private final Set<NamespacedKey> recipes = new HashSet<>();
    public static final Random RANDOM = new Random();
    @Override
    public void onEnable() {
        plugin = this;
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        EggManager.getEggManager().loadEggs();
        getServer().getPluginManager().registerEvents(new OnOpJoinEvent(), this);

        GetEgg testEgg = new GetEgg();
        PluginCommand testEggCmd = getCommand("giveegg");

        testEggCmd.setExecutor(testEgg);
        testEggCmd.setTabCompleter(testEgg);


        new UpdateChecker().getVersion(version -> {
            String curr = "\""+getDescription().getVersion()+"\"";
            if (version.replaceFirst("r\":\"[0-9]\\.[0-9]+\\.[0-9]+(\\.[0-9]+)?\"", "r\":"+curr).equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().warning("There is a new update available!");
                getLogger().warning("Go to https://modrinth.com/plugin/crazy-eggs in order to update!");
            }
        });
    }
    private Permission getPermission(String name) {
        return getServer().getPluginManager().getPermission(name);
    }
    public Permission addParentPermission(String name, PermissionDefault def, @Nullable String description) {
        Permission permission = getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
            permission.setDefault(def);
            if (description != null)
                permission.setDescription(description);
            getServer().getPluginManager().addPermission(permission);
        }
        return permission;
    }
    public Permission addPermission(String name, PermissionDefault def, @Nullable String description) {
        Permission permission = getPermission(name);
        if (permission == null) {
            permission = new Permission(name);
            permission.setDefault(def);
            if (description != null)
                permission.setDescription(description);
            permission.addParent(name.replaceAll("\\.(.*)", ".*"), true);
            getServer().getPluginManager().addPermission(permission);
        }
        return permission;
    }
    @Override
    public void onDisable() {
        recipes.forEach(recipe -> getServer().removeRecipe(recipe));
    }
    public static CrazyEggs getPlugin() {
        return plugin;
    }
    public Set<NamespacedKey> getRecipes() {
        return this.recipes;
    }
}