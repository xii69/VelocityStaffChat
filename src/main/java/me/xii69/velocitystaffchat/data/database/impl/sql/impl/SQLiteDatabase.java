package me.xii69.velocitystaffchat.data.database.impl.sql.impl;


import com.moandjiezana.toml.Toml;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import me.xii69.velocitystaffchat.data.database.impl.sql.AbstractSqlDatabase;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CompletableFuture;

public abstract class SQLiteDatabase extends AbstractSqlDatabase {

    private File file;

    @Override
    public CompletableFuture<Boolean> enable(VelocityStaffChat plugin, Toml properties) {
        String name = properties.getString("database");
        File folder = plugin.getDataFolder();

        return associate(() -> {
            file = new File(folder, name + ".db");

            if (!file.exists()) {
                createFile(file);
            }
        }).thenCompose(value -> super.enable(properties));
    }

    @Override
    public String getName() {
        return "sqlite";
    }

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void createFile(File file) {
        if (file == null) {
            return;
        }

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ignored) {
                // Ignored, return false
            }
        }

    }

}
