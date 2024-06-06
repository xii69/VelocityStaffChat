package me.xii69.bukkitstaffchat.database.registry;

import me.xii69.bukkitstaffchat.database.Database;
import me.xii69.bukkitstaffchat.database.databases.RedisChatCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DatabaseRegistry {

    private final JavaPlugin plugin;
    private final FileConfiguration configuration;
    private final Map<String, RegisteredDatabase> databases = new HashMap<>();

    public DatabaseRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfig();
    }

    public CompletableFuture<Void> registerDefaults() {
        return register("chat", DatabaseProvider.of(new RedisChatCache()));
    }

    public CompletableFuture<Void> register(String name, DatabaseProvider provider) {
        databases.put(name, new RegisteredDatabase(name, provider));
        return tryLoad(databases.get(name));
    }

    public <T extends Database> T getStorage(String name, Class<T> databaseClass) {
        RegisteredDatabase registeredDatabase = databases.get(name);

        if (registeredDatabase == null) {
            System.out.println("Database " + name + " is not registered!");
            return null;
        }

        Database database = registeredDatabase.getDatabase();

        if (database == null) {
            System.out.println("Database " + name + " is not enabled!");
            return null;
        }

        if (databaseClass.isInstance(database)) {
            return databaseClass.cast(database);
        }

        System.out.println("Database " + name + " is not of type " + databaseClass.getSimpleName() + "!");
        return null;
    }

    public CompletableFuture<Void> tryLoad(RegisteredDatabase database) {
        if (database.isEnabled()) {
            return CompletableFuture.completedFuture(null);
        }

        String databaseType = configuration.getString("databases." + database.getName() + ".type");
        ConfigurationSection section = configuration.getConfigurationSection("databases." + database.getName() + ".credentials");

        if (section == null) {
            System.out.println("Failed to load database " + database.getName() + " because credentials are not set!");
            return CompletableFuture.completedFuture(null);
        }

        Database toEnable = database.getProvider().getDatabase(databaseType);
        return toEnable.enable(plugin, section).thenAccept(value -> {
            if (value) {
                database.setEnabled(true);
                database.setDatabase(toEnable);
            } else {
                System.out.println("Failed to enable database " + database.getName());
            }
        });
    }
}
