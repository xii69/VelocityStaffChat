package me.xii69.velocitystaffchat;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.Command;
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

public class VelocityStaffChat implements SimpleCommand {
    private final Toml toml;
    private final boolean enabled;
    private final Logger logger;
    private final String prefix;
    private final String webhook;
    private final String toggleFormat;
    private final String messageFormat;
    private final Set<UUID> toggledPlayers;
    private final String webhookMessageFormat;
    private final Metrics.Factory metricsFactory;
    public ProxyServer server;
    private Path path;

    @Inject
    public VelocityStaffChat(ProxyServer server, Logger logger, Metrics.Factory metricsFactory, @DataDirectory Path path) {
        this.server = server;
        this.logger = logger;
        this.path = path;
        this.toml = loadConfig(path);
        this.metricsFactory = metricsFactory;
        this.prefix = toml.getString("Configuration.Prefix");
        this.toggleFormat = toml.getString("Messages.Toggle-Format");
        this.messageFormat = toml.getString("Messages.Message-Format");
        this.toggledPlayers = new HashSet<>();
        this.webhook = toml.getString("Discord.Webhook");
        this.enabled = toml.getBoolean("Discord.Enabled");
        this.webhookMessageFormat = toml.getString("Discord.Message-Format");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (toml == null) {
            logger.warn("Failed to load config.toml, disabling VelocityStaffChat ...");
            return;
        }

        registerCommand("staffchat", new ArrayList<>(List.of("staffchat", "sc")), this, this.server);
        Metrics metrics = metricsFactory.make(this, 20659);
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(colorize(toml.getString("Messages.Only-Players")));
            return;
        }

        if (!source.hasPermission("velocitystaffchat.staff")) {
            source.sendMessage(colorize(toml.getString("Messages.No-Permission")));
            return;
        }

        Player player = (Player) source;

        if (args.length != 0) {
            sendStaffMessage(player, player.getCurrentServer().get(), String.join(" ", args));
            return;
        }

        if (toggledPlayers.contains(player.getUniqueId())) {
            toggledPlayers.remove(player.getUniqueId());
            sendToggleMessage(player, false);
        } else {
            toggledPlayers.add(player.getUniqueId());
            sendToggleMessage(player, true);
        }
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (toggledPlayers.contains(player.getUniqueId())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            sendStaffMessage(player, player.getCurrentServer().get(), event.getMessage());
            if (enabled) sendDiscordMessage(player, player.getCurrentServer().get(), event.getMessage(), webhook);
        } else if (String.valueOf(event.getMessage().charAt(0)).equalsIgnoreCase(prefix) && (player.hasPermission("velocitystaffchat.staff"))) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            sendStaffMessage(player, player.getCurrentServer().get(), event.getMessage().substring(1));
            if (enabled) sendDiscordMessage(player, player.getCurrentServer().get(), event.getMessage().substring(1), webhook);
        }
    }

    private void sendToggleMessage(Player player, boolean state) {
        player.sendMessage(colorize(toggleFormat.replace("{state}", state ? "enabled" : "disabled")));
    }

    private void sendStaffMessage(Player player, ServerConnection server, String message) {
        this.server.getAllPlayers()
                .stream()
                .filter(target -> target.hasPermission("velocitystaffchat.staff"))
                .forEach(target -> target.sendMessage(
                        colorize(
                                messageFormat.replace("{player}", player.getUsername())
                                        .replace("{server}", server != null ? server.getServerInfo().getName() : "N/A")
                                        .replace("{message}", message)
                        )
                ));
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

    public TextComponent colorize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public void registerCommand(String name, Collection<String> aliases, Command command, ProxyServer server) {
        server.getCommandManager().register(server.getCommandManager().metaBuilder(name).aliases(aliases.toArray(new String[0])).build(), command);
    }

    public void sendDiscordMessage(Player player, ServerConnection server, String message, String url) {
        String finalMessage = new String(webhookMessageFormat.replace("{player}", player.getUsername())
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
