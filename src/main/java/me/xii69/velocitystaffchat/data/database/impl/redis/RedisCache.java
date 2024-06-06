package me.xii69.velocitystaffchat.data.database.impl.redis;

import com.moandjiezana.toml.Toml;
import me.xii69.velocitystaffchat.data.database.AbstractDatabase;
import me.xii69.velocitystaffchat.data.database.impl.type.DatabaseType;
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
    public CompletableFuture<Boolean> enable(Toml config) {
        return CompletableFuture.supplyAsync(() -> {
            Config reddissonConfig = new Config();
            reddissonConfig.useSingleServer()
                    .setAddress("redis://" + config.getString("host") + ":" + config.getString("port"))
                    .setPassword(config.getString("password"));

            redissonClient = Redisson.create(reddissonConfig);
            return true;
        });
    }
}
