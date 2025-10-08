package me.choketa.crazyeggs.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final LegacyComponentSerializer SERIALIZER;
    static {
        SERIALIZER = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build();
    }


    public static Component format(String str) {
        if (str == null) return null;
        return SERIALIZER.deserialize(str);
    }
}

