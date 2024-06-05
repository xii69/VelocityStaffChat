package dev.imlukas.bukkitstaffchat.database.impl.sql;

import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractRemoteSqlDatabase extends AbstractSqlDatabase {

    protected String host;
    protected int port;
    protected String database;
    protected String username;
    protected String password;

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection properties) {
        host = properties.getString("host");
        port = properties.getInt("port");
        database = properties.getString("database");
        username = properties.getString("username");
        password = properties.getString("password");

        return super.enable(properties);
    }
}
