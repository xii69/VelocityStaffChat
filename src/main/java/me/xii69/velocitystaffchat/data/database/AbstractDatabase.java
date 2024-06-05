package me.xii69.velocitystaffchat.data.database;

import com.google.common.collect.Sets;
import com.moandjiezana.toml.Toml;
import me.xii69.velocitystaffchat.VelocityStaffChat;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractDatabase implements Database {

    private final Set<CompletableFuture<?>> futures = Sets.newConcurrentHashSet();

    @Override
    public CompletableFuture<Void> flush() {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    protected <T> CompletableFuture<T> addFuture(CompletableFuture<T> future) {
        futures.add(future);
        return future.whenComplete((v, e) -> futures.remove(future));
    }

    protected <T> CompletableFuture<T> associate(Supplier<T> supplier) {
        return addFuture(CompletableFuture.supplyAsync(supplier));
    }

    protected CompletableFuture<Void> associate(Runnable runnable) {
        return addFuture(CompletableFuture.runAsync(runnable));
    }

    protected <T> CompletableFuture<T> associate(CompletableFuture<T> future) {
        return addFuture(future);
    }

    @Override
    public CompletableFuture<Boolean> enable(VelocityStaffChat plugin, Toml config) {
        return enable(config);
    }
}
