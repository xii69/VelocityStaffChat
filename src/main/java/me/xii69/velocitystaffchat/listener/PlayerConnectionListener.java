package me.xii69.velocitystaffchat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import me.xii69.velocitystaffchat.registry.PlayerDataRegistry;

public class PlayerConnectionListener {

    private final PlayerDataRegistry playerDataRegistry;

    public PlayerConnectionListener(VelocityStaffChat plugin) {
        this.playerDataRegistry = plugin.getPlayerDataRegistry();
    }

    @Subscribe
    public void onJoin(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        playerDataRegistry.register(player.getUniqueId());
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        playerDataRegistry.unregister(player.getUniqueId());
    }
}
