package dev.imlukas.bukkitstaffchat.database.impl.redis;

import dev.imlukas.bukkitstaffchat.database.impl.type.DatabaseType;
import dev.imlukas.bukkitstaffchat.database.AbstractDatabase;
import org.bukkit.configuration.ConfigurationSection;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RedisCache extends AbstractDatabase {

    private RedissonClient redissonClient;

    @Override
    public DatabaseType getType() {
        return DatabaseType.CACHING;
    }

    @Override
    public String getName() {
        return "redis";
    }

    protected <T> CompletableFuture<T> query(Function<RedissonClient, T> function) {
        return associate(() -> function.apply(redissonClient));
    }

    protected CompletableFuture<Void> update(Consumer<RedissonClient> consumer) {
        return associate(() -> consumer.accept(redissonClient));
    }

    protected void useRedis(Consumer<RedissonClient> consumer) {
        consumer.accept(redissonClient);
    }

    protected RedissonClient getJedis() {
        return redissonClient;
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection config) {
        return CompletableFuture.supplyAsync(() -> {
            Config reddissonConfig = new Config();
            reddissonConfig.useSingleServer()
                    .setAddress("redis://" + config.getString("host") + ":" + config.getLong("port"))
                    .setPassword(config.getString("password"));

            redissonClient = Redisson.create(reddissonConfig);

            return true;
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return false;
        });
    }
}
