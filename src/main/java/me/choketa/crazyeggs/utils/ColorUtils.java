package me.choketa.crazyeggs.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    private static final LegacyComponentSerializer SERIALIZER;
    static {
        SERIALIZER = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .build();
    }
    private static final MiniMessage MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .build()
            )
            .build();


    public static Component format(String str) {
        if (str == null) return null;
        return SERIALIZER.deserialize(SERIALIZER.serialize(MINIMESSAGE.deserialize(str)));
    }
}

