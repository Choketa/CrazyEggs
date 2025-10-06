package me.choketa.crazyeggs.listeners;

import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;
import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class OnOpJoinEvent implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;
        new UpdateChecker().getVersion(version -> {
            String curr = "\""+getPlugin().getDescription().getVersion()+"\"";
            if (!version.replaceFirst("\"[0-9]\\.[0-9]\\.[0-9]\"", curr).equals(version)) {
                event.getPlayer().sendMessage(format("&4[CrazyEggs]: &6There is a new update available!"));
                event.getPlayer().sendMessage(format("&4[CrazyEggs]: &6Go to https://modrinth.com/plugin/crazy-eggs in order to update!"));
            }
            getPlugin().getLogger().warning(version);
        });
    }
}
