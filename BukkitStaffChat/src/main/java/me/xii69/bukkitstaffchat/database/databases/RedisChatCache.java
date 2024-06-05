package me.xii69.bukkitstaffchat.database.databases;

import me.xii69.bukkitstaffchat.database.impl.redis.RedisCache;
import org.redisson.api.RMap;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisChatCache extends RedisCache implements ChatStorage {

    @Override
    public CompletableFuture<Boolean> isToggled(UUID playerId) {
        return query(client -> {
            RMap<String, Boolean> map = client.getMap("staff-chat");
            boolean toggled = map.getOrDefault(playerId.toString(), false);
            map.destroy();
            return toggled;
        });
    }

    @Override
    public CompletableFuture<Void> setToggled(UUID playerId, boolean toggled) {
        return update(client -> {
            RMap<String, Boolean> map = client.getMap("staff-chat");
            map.put(playerId.toString(), toggled);
            map.destroy();
        });
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return update(client -> {
            RMap<String, Boolean> map = client.getMap("staff-chat");
            map.clear();
            map.destroy();
        });
    }
}
