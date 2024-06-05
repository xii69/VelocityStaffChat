package me.xii69.velocitystaffchat.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import me.xii69.velocitystaffchat.VelocityStaffChat;

public class ProxyInitializeListener {

    private final VelocityStaffChat plugin;

    public ProxyInitializeListener(VelocityStaffChat plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (plugin.getConfiguration() == null) {
            plugin.getLogger().warn("Failed to load config.toml, disabling VelocityStaffChat ...");
            return;
        }


        System.out.println("Registered staffchat command!");
    }
}
