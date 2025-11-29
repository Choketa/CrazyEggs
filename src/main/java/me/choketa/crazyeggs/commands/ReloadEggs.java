package me.choketa.crazyeggs.commands;

import me.choketa.crazyeggs.eggs.EggManager;
import me.choketa.crazyeggs.eggs.PluginEgg;
import me.choketa.crazyeggs.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.choketa.crazyeggs.eggs.EggManager.getEggManager;

public class ReloadEggs implements CommandExecutor{
    private final EggManager manager;

    public ReloadEggs() {
        this.manager = getEggManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        manager.getEggs().forEach(PluginEgg::reload);
        sender.sendMessage(ColorUtils.format("&4[CrazyEggs] &fSuccessfully reloaded all of the eggs!"));
        return true;
    }
}
