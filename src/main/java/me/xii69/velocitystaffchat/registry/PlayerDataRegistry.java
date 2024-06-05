package me.xii69.velocitystaffchat.registry;

import me.xii69.velocitystaffchat.data.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataRegistry {

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerData get(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public PlayerData register(UUID playerId) {
        return playerDataMap.put(playerId, new PlayerData(playerId));
    }

    public void unregister(UUID playerId) {
        playerDataMap.remove(playerId);
    }

}
