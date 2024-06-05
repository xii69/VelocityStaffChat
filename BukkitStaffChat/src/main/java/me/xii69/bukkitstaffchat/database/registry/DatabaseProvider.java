package me.xii69.bukkitstaffchat.database.registry;

import me.xii69.bukkitstaffchat.database.Database;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider {

    private final List<Database> databases = new ArrayList<>();

    public DatabaseProvider(List<Database> databases) {
        this.databases.addAll(databases);
    }

    public static DatabaseProvider of(Database... databases) {
        return new DatabaseProvider(List.of(databases));
    }

    public Database getDatabase(String name) {
        for (Database database : databases) {
            if (database.getName().equalsIgnoreCase(name)) {
                return database;
            }
        }
        return null;
    }
}
