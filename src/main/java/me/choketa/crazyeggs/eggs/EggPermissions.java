package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.eggs.eggs.PluginEgg;
import org.bukkit.permissions.PermissionDefault;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class EggPermissions {
    private final PluginEgg egg;
    private final String prefix;
    public EggPermissions(PluginEgg egg) {
        this.egg = egg;
        this.prefix = "crazyeggs."+egg.getSimpleName()+".";
        getPlugin().addParentPermission(prefix+"*", PermissionDefault.OP, "Wildcard");
        getPlugin().addPermission(prefix+"getegg", PermissionDefault.OP, "Allows you to obtain the specific egg via command");
        getPlugin().addPermission(prefix+"craft", PermissionDefault.TRUE, "Allows you to craft the specified egg");
        getPlugin().addPermission(prefix+"use", PermissionDefault.TRUE, "Allows you to use the specified egg");
        getPlugin().addPermission(prefix+"bypass.cooldown", PermissionDefault.OP, "Allows you to bypass the cooldown of the specific egg");
    }
}
