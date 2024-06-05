package me.xii69.velocitystaffchat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import me.xii69.velocitystaffchat.data.PlayerData;
import me.xii69.velocitystaffchat.registry.PlayerDataRegistry;
import me.xii69.velocitystaffchat.settings.PluginSettings;

import java.util.Optional;

public class PlayerChatListener{

    private final VelocityStaffChat plugin;
    private final PluginSettings settings;
    private final PlayerDataRegistry playerDataRegistry;

    public PlayerChatListener(VelocityStaffChat plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Optional<ServerConnection> serverOptional = player.getCurrentServer();

        if (serverOptional.isEmpty()) {
            return;
        }

        ServerConnection server = serverOptional.get();
        PlayerData playerData = playerDataRegistry.get(player.getUniqueId());

        if (playerData == null) {
            return;
        }

        String message = event.getMessage();

        if (playerData.isToggled() || (doesMessageHavePrefix(message) && player.hasPermission("velocitystaffchat.staff"))) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            sendMessage(player, server, message);
        }
    }

    public void sendMessage(Player player, ServerConnection server, String message) {
        plugin.sendStaffMessage(player, server, message);

        if (settings.isDiscordEnabled()) {
            plugin.sendDiscordMessage(player, server, message, settings.getWebhook());
        }
    }

    public boolean doesMessageHavePrefix(String message) {
        return message.startsWith(settings.getPrefix());
    }
}
