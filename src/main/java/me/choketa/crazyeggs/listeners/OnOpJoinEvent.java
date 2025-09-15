package me.choketa.crazyeggs.listeners;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.utils.UpdateChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class OnOpJoinEvent implements Listener {
    private final CrazyEggs plugin;
    public OnOpJoinEvent(CrazyEggs plugin) {
        this.plugin=plugin;
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (!event.getPlayer().isOp()) return;
        new UpdateChecker(plugin, 111676).getVersion(version -> {
            if (!plugin.getDescription().getVersion().equals(version)) {
                event.getPlayer().sendMessage(format("&4[CrazyEggs]: &6There is a new update available!"));
                event.getPlayer().sendMessage(format("&4[CrazyEggs]: &6Go to https://www.spigotmc.org/resources/1-20-crazyeggs.111676/ in order to update!"));
            }
        });
    }
}
