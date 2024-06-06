package me.xii69.velocitystaffchat.data.database.impl.sql.impl;

import me.xii69.velocitystaffchat.data.database.impl.sql.AbstractRemoteSqlDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public abstract class MySQLDatabase extends AbstractRemoteSqlDatabase {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    protected Connection createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Properties properties = new Properties();

            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");

            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
