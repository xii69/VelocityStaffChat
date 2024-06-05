package me.xii69.bukkitstaffchat;

import me.xii69.bukkitstaffchat.database.databases.ChatStorage;
import me.xii69.bukkitstaffchat.database.registry.DatabaseRegistry;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BukkitStaffChat extends JavaPlugin {

    private static BukkitStaffChat instance;
    private ChatStorage chatStorage;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        DatabaseRegistry databaseRegistry = new DatabaseRegistry(this);
        databaseRegistry.registerDefaults().thenRun(() -> this.chatStorage = databaseRegistry.getStorage("chat", ChatStorage.class));
        System.out.println("BukkitStaffChat has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BukkitStaffChat getInstance() {
        return instance;
    }
}
