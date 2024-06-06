package me.xii69.velocitystaffchat.data.database.impl.sql;

import com.moandjiezana.toml.Toml;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractRemoteSqlDatabase extends AbstractSqlDatabase {

    protected String host;
    protected long port;
    protected String database;
    protected String username;
    protected String password;

    @Override
    public CompletableFuture<Boolean> enable(Toml properties) {
        host = properties.getString("host");
        port = properties.getLong("port");
        database = properties.getString("database");
        username = properties.getString("username");
        password = properties.getString("password");

        return super.enable(properties);
    }
}
