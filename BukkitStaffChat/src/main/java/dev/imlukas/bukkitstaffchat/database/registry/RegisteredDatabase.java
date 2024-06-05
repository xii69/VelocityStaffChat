package dev.imlukas.bukkitstaffchat.database.registry;

import dev.imlukas.bukkitstaffchat.database.Database;

public class RegisteredDatabase {

    private final String name;
    private final DatabaseProvider provider;
    private boolean isEnabled;
    private Database database;

    public RegisteredDatabase(String name, DatabaseProvider provider) {
        this.name = name;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public DatabaseProvider getProvider() {
        return provider;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
