package dev.imlukas.bukkitstaffchat.database.databases;

import dev.imlukas.bukkitstaffchat.database.Database;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ChatStorage extends Database {

    CompletableFuture<Boolean> isToggled(UUID playerId);

    CompletableFuture<Void> setToggled(UUID playerId, boolean toggled);
}
