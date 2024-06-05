package me.xii69.velocitystaffchat.data.database;

import com.moandjiezana.toml.Toml;
import me.xii69.velocitystaffchat.VelocityStaffChat;
import me.xii69.velocitystaffchat.data.database.impl.type.DatabaseType;

import java.util.concurrent.CompletableFuture;

public interface Database {

    DatabaseType getType();

    String getName();


    CompletableFuture<Boolean> enable(VelocityStaffChat plugin, Toml config);

    CompletableFuture<Boolean> enable(Toml config);

    CompletableFuture<Void> flush();

    CompletableFuture<Void> wipe();
}
