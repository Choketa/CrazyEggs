package me.choketa.crazyeggs.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class UpdateChecker {


    public UpdateChecker() {
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/NfCmfKoW/version");
                InputStream in = url.openStream();

                String version = new Scanner(in).nextLine();
                consumer.accept(version);
                in.close();
            } catch (IOException e) {
                getPlugin().getLogger().warning("Unable to fetch version");
            }
        });
    }
}