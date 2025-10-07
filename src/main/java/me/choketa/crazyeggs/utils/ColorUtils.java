package me.choketa.crazyeggs.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    public static String format(String str) {
        if (str == null) return null;
        return color(hexColor(str));
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    private static String hexColor(String str) {
        Matcher match = HEX_PATTERN.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start(), match.end());
            str = str.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color.substring(1))));
            match = HEX_PATTERN.matcher(str);
        }
        return str;
    }

    private static String color(String str) {
        return formatWithCode(str);
    }

    private static String formatWithCode(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}

