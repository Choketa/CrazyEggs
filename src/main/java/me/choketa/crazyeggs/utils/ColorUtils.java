package me.choketa.crazyeggs.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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

