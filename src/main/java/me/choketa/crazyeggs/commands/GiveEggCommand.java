package me.choketa.crazyeggs.commands;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.recipes.CrazyEggRecipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class GiveEggCommand implements CommandExecutor {

    CrazyEggRecipe egg;
    CrazyEggs plugin;

    public GiveEggCommand(CrazyEggs plugin) {
        this.plugin = plugin;
        egg = new CrazyEggRecipe(this.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ItemStack item = egg.eggItem().clone();

        if (args.length >= 3) {
            sender.sendMessage(ChatColor.RED+"[CrazyEggs] Proper usage: "+ChatColor.YELLOW+"/getegg <player> amount");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED+"[CrazyEggs] You must specify a player");
            return true;
        }
        Player exactPlayer = Bukkit.getPlayerExact(args[0]);
        if (exactPlayer == null || !exactPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED+"[CrazyEggs] No player was found");
            return true;
        }
        if (args.length == 1 ) {
            item.setAmount(1);
            exactPlayer.getInventory().addItem(item);
            sender.sendMessage(format("&4[CrazyEggs] &fGave 1 Crazy Egg to "+exactPlayer.getName()));
            exactPlayer.playSound(exactPlayer, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
            return true;
        }
        try {
            item.setAmount(Integer.parseInt(args[1]));
            exactPlayer.getInventory().addItem(item);
            sender.sendMessage(format("&4[CrazyEggs] &fGave " + Integer.parseInt(args[1]) + " Crazy Egg to " + exactPlayer.getName()));
            exactPlayer.playSound(exactPlayer, Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED+"[CrazyEggs] Proper usage: "+ChatColor.YELLOW+"/getegg <player> amount");
            return true;
        }
        return true;
    }
}

