package me.choketa.crazyeggs.utils;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String format(String arg) {
            return ChatColor.translateAlternateColorCodes('&', arg);
        }
    }

