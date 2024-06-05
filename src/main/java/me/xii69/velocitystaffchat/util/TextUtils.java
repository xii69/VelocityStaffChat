package me.xii69.velocitystaffchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;

/**
 * Represents a utility class for text.
 */
public final class TextUtils {

    private static final Map<String, String> LEGACY_TO_COMPONENT = Map.ofEntries(
            Map.entry("f", "<white>"),
            Map.entry("7", "<gray>"),
            Map.entry("8", "<dark_gray>"),
            Map.entry("b", "<aqua>"),
            Map.entry("3", "<dark_aqua>"),
            Map.entry("9", "<blue>"),
            Map.entry("1", "<dark_blue>"),
            Map.entry("d", "<light_purple>"),
            Map.entry("5", "<dark_purple>"),
            Map.entry("e", "<yellow>"),
            Map.entry("6", "<gold>"),
            Map.entry("a", "<green>"),
            Map.entry("2", "<dark_green>"),
            Map.entry("c", "<red>"),
            Map.entry("4", "<dark_red>"),
            Map.entry("l", "<bold>"),
            Map.entry("n", "<underline>"),
            Map.entry("o", "<italic>"),
            Map.entry("m", "<strikethrough>"),
            Map.entry("k", "<obfuscated>"),
            Map.entry("r", "<reset>")
    );

    private TextUtils() {
    }

    /**
     * Deserializes a String to a Component using MiniMessage.
     *
     * @param message The String to deserialize
     * @return The deserialized Component
     */
    public static Component color(String message) {
        return MiniMessage.miniMessage().deserialize(legacyToComponent(message)).decoration(TextDecoration.ITALIC, false);
    }

    private static String legacyToComponent(String legacy) {
        for (Map.Entry<String, String> entry : LEGACY_TO_COMPONENT.entrySet()) {
            legacy = legacy.replaceAll("[&§](" + entry.getKey() + ")", entry.getValue());
        }

        return legacy;
    }
}

