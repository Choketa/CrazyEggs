package me.choketa.crazyeggs.utils;

import com.google.gson.*;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class UpdateChecker {


    public UpdateChecker() {}

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                URL url = new URI("https://api.modrinth.com/v2/project/NfCmfKoW/version").toURL();
                InputStream in = url.openStream();

                String version = new Scanner(in).nextLine();
                consumer.accept(version);
                in.close();
            } catch (IOException | URISyntaxException e) {
                getPlugin().getLogger().warning("Unable to fetch version");
            }
        });
    }
}