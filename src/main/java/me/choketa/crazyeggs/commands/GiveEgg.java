package me.choketa.crazyeggs.commands;

import me.choketa.crazyeggs.eggs.EggManager;
import me.choketa.crazyeggs.eggs.PluginEgg;
import me.choketa.crazyeggs.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.choketa.crazyeggs.eggs.EggManager.getEggManager;
import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class GiveEgg implements CommandExecutor, TabCompleter {
    private final EggManager manager;
    public GiveEgg() {
        this.manager = getEggManager();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 4) {
            sender.sendMessage(format("&c[CrazyEggs] Proper usage: &e/getegg <player> <egg> <amount>"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(format("&c[CrazyEggs] You must specify a player"));
            return true;
        }
        Player exactPlayer = Bukkit.getPlayerExact(args[0]);
        if (exactPlayer == null || !exactPlayer.isOnline()) {
            sender.sendMessage(format("&c[CrazyEggs] No player was found"));
            return true;
        }
        if (args.length == 1) {
            sender.sendMessage(format("&c[CrazyEggs] You must specify an egg"));
            return true;
        }
        PluginEgg egg = manager.getEggByName(args[1]);
        if (egg == null) {
            sender.sendMessage(format("&c[CrazyEggs] No egg was found"));
            return true;
        }
        ItemStack eggStack = egg.getEggItem();
        if (args.length == 2) {
            eggStack.setAmount(1);
            exactPlayer.getInventory().addItem(eggStack);
            sender.sendMessage(format("&4[CrazyEggs] &fGave 1 "+egg.getDisplayName()+" to "+exactPlayer.getName()));
            exactPlayer.playSound(exactPlayer, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
            return true;
        }
        try {
            int parse = Integer.parseInt(args[2]);
            eggStack.setAmount(parse);
            exactPlayer.getInventory().addItem(eggStack);
            sender.sendMessage(format("&4[CrazyEggs] &fGave " + parse + " "+egg.getDisplayName()+" to " + exactPlayer.getName()));
            exactPlayer.playSound(exactPlayer, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        } catch (NumberFormatException e) {
            sender.sendMessage(format("&c[CrazyEggs] Proper usage: &e/getegg <player> <egg> <amount>"));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 2)
            return manager.getEggs().stream().map(PluginEgg::getName).toList();
        return null;
    }
}
