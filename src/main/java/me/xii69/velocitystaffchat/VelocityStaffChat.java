package me.xii69.velocitystaffchat;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandMeta;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "velocitystaffchat",
        name = "VelocityStaffChat",
        version = "1.0.0",
        description = "VelocityStaffChat plugin by xii69.",
        authors = {"xii69"}
)

public class VelocityStaffChat implements SimpleCommand {

    @Inject
    private Logger logger;
    @Inject
    public ProxyServer proxyServer;
    @Inject
    @DataDirectory
    private Path configPath;

    private Toml toml;
    private String prefix;
    private String toggleFormat;
    private String messageFormat;
    private Set<UUID> toggledPlayers;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.toml = loadConfig(configPath);
        if (toml == null) {
            logger.warn("Failed to load config.toml, Shutting down.");
            return;
        }
        this.prefix = toml.getString("Prefix");
        this.toggleFormat = toml.getString("Toggle-Format");
        this.messageFormat = toml.getString("Message-Format");
        this.toggledPlayers = new HashSet<UUID>();
        registerCommand("staffchat", new ArrayList<>(List.of("staffchat", "sc")), this, this.proxyServer);
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();
        if (source instanceof Player) {
            Player player = (Player) source;
            if (player.hasPermission("staffchat")) {
                if (args.length == 0) {
                    if (toggledPlayers.contains(player.getUniqueId())) {
                        toggledPlayers.remove(player.getUniqueId());
                        sendToggleMessage(player, false);
                    } else {
                        toggledPlayers.add(player.getUniqueId());
                        sendToggleMessage(player, true);
                    }
                } else {
                    sendStaffMessage(player, player.getCurrentServer().get(), String.join(" ", args));
                }
            } else {
                player.sendMessage(colorize(toml.getString("No-Permission")));
            }
        } else {
            source.sendMessage(colorize(toml.getString("Only-Players")));
        }
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (toggledPlayers.contains(player.getUniqueId())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            sendStaffMessage(player, player.getCurrentServer().get(), event.getMessage());
        } else if (String.valueOf(event.getMessage().charAt(0)).equalsIgnoreCase("#") && (player.hasPermission("staffchat"))) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            sendStaffMessage(player, player.getCurrentServer().get(), event.getMessage().substring(1));
        }
    }

    private void sendToggleMessage(Player player, boolean state) {
        player.sendMessage(colorize(toggleFormat.replace("{state}", state ? "enabled" : "disabled")));
    }

    private void sendStaffMessage(Player player, ServerConnection server, String message) {
        proxyServer.getAllPlayers().stream().filter(target -> target.hasPermission("staffchat")).forEach(target -> {
            target.sendMessage(colorize(messageFormat.replace("{player}", player.getUsername())
                    .replace("{server}", server != null ? server.getServerInfo().getName() : "N/A").replace("{message}", message)));
        });
    }

    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
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
        CommandMeta meta = server.getCommandManager().metaBuilder(name).aliases(aliases.toArray(new String[0])).build();
        server.getCommandManager().register(meta, command);
    }

}
