package me.xii69.velocitystaffchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class TextUtils {

    private TextUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static Component colorize(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }
}
