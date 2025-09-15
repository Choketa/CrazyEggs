package me.choketa.crazyeggs.utils;

import me.choketa.crazyeggs.CrazyEggs;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final CrazyEggs plugin;
    private final int resourceId;
    private BukkitTask task;

    public UpdateChecker(CrazyEggs plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        task = Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream is = new URI("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).toURL().openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext())
                    consumer.accept(scann.next());

            } catch (IOException e) {
                plugin.getLogger().info("Unable to check for updates: " + e.getMessage());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void onDisable() {
        task.cancel();
    }
}
