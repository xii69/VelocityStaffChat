package me.xii69.velocitystaffchat.data.database.registry;

import com.moandjiezana.toml.Toml;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import me.xii69.velocitystaffchat.data.database.Database;
import me.xii69.velocitystaffchat.data.database.databases.RedisChatCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DatabaseRegistry {

    private final VelocityStaffChat plugin;
    private final Toml configuration;
    private final Map<String, RegisteredDatabase> databases = new HashMap<>();

    public DatabaseRegistry(VelocityStaffChat plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfiguration();
    }

    public CompletableFuture<Boolean> registerDefaults() {
        return register("chat", DatabaseProvider.of(new RedisChatCache()));
    }

    public CompletableFuture<Boolean> register(String name, DatabaseProvider provider) {
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

    public CompletableFuture<Boolean> tryLoad(RegisteredDatabase database) {
        if (database.isEnabled()) {
            return CompletableFuture.completedFuture(true);
        }

        String databaseType = configuration.getString("databases." + database.getName() + ".type");

        if (databaseType == null || databaseType.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        Toml section = configuration.getTable("databases." + database.getName() + ".credentials");

        if (section == null) {
            System.out.println("Failed to load database " + database.getName() + " because credentials are not set!");
            return CompletableFuture.completedFuture(false);
        }

        Database toEnable = database.getProvider().getDatabase(databaseType);
        return toEnable.enable(plugin, section).thenApply(value -> {
            if (value) {
                database.setEnabled(true);
                database.setDatabase(toEnable);
            } else {
                System.out.println("Failed to enable database " + database.getName());
            }

            return value;
        });
    }
}
