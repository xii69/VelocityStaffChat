package me.xii69.velocitystaffchat;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import lombok.Getter;
import me.xii69.velocitystaffchat.command.StaffChatCommand;
import me.xii69.velocitystaffchat.data.PlayerData;
import me.xii69.velocitystaffchat.listener.PlayerChatListener;
import me.xii69.velocitystaffchat.listener.PlayerConnectionListener;
import me.xii69.velocitystaffchat.listener.ProxyInitializeListener;
import me.xii69.velocitystaffchat.registry.PlayerDataRegistry;
import me.xii69.velocitystaffchat.settings.PluginSettings;
import me.xii69.velocitystaffchat.util.TextUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Plugin(
        id = "velocitystaffchat",
        name = "VelocityStaffChat",
        version = "1.2.0",
        description = "Simple, Fast & Lightweight Staff Chat plugin for Velocity",
        authors = {"xii69"}
)

@Getter
public class VelocityStaffChat {

    private static VelocityStaffChat instance; // Used for API purposes, not be used in the plugin itself
    private final Toml toml;
    private final Logger logger;
    private final ProxyServer server;
    private final CommandManager commandManager;

    private final PluginSettings settings;
    private final PlayerDataRegistry playerDataRegistry;

    @Inject
    public VelocityStaffChat(ProxyServer server, Logger logger, @DataDirectory Path path) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.toml = loadConfig(path);

        this.commandManager = server.getCommandManager();
        this.playerDataRegistry = new PlayerDataRegistry();
        this.settings = new PluginSettings(this);

        new PlayerChatListener(this);
        new PlayerConnectionListener(this);
        new ProxyInitializeListener(this);
    }

    public static VelocityStaffChat getInstance() {
        return instance;
    }

    public Toml getConfiguration() {
        return toml;
    }

    private Toml loadConfig(Path path) {
        File file = new File(path.toFile(), "config.toml");

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) return null;

        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                Files.copy(input != null ? input : new ByteArrayInputStream(new byte[0]), file.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }

        return new Toml().read(file);
    }

    public void registerCommand(String name, Command command, String... aliases) {
        commandManager.register(commandManager.metaBuilder(name).aliases(aliases).build(), command);
    }

    public void sendToggleMessage(Player player, boolean state) {
        player.sendMessage(TextUtils.colorize(settings.getToggleFormat().replace("{state}", state ? "enabled" : "disabled")));
    }

    public void sendStaffMessage(Player player, ServerConnection server, String message) {
        this.server.getAllPlayers()
                .stream()
                .filter(target -> target.hasPermission("velocitystaffchat.staff"))
                .forEach(target -> target.sendMessage(
                        TextUtils.colorize(
                                settings.getMessageFormat().replace("{player}", player.getUsername())
                                        .replace("{server}", server != null ? server.getServerInfo().getName() : "N/A")
                                        .replace("{message}", message)
                        )
                ));
    }

    public void sendDiscordMessage(Player player, ServerConnection server, String message, String url) {
        String finalMessage = new String(settings.getWebhookMessageFormat().replace("{player}", player.getUsername())
                .replace("{server}", server != null ? server.getServerInfo().getName() : "N/A")
                .replace("{message}", message.replace("&", "")).getBytes(), StandardCharsets.UTF_8
        );

        CompletableFuture.runAsync(() -> {
            try {
                final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    String preparedCommand = finalMessage.replaceAll("\\\\", "");
                    if (preparedCommand.endsWith(" *"))
                        preparedCommand = preparedCommand.substring(0, preparedCommand.length() - 2) + "*";
                    outputStream.write(("{\"content\":\"" + preparedCommand + "\"}").getBytes(StandardCharsets.UTF_8));
                }
                connection.getInputStream();
            } catch (final IOException e) {
                logger.warn("Failed to send staff chat message via webhook!");
            }
        });
    }

}
