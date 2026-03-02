package com.ivan.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericCache<K, V> {
    private final Cache<K, V> cache;

    public GenericCache(Duration expiration) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(expiration)
                .build();
    }

    public void load(Supplier<Map<K, V>> loadSupplier) {
        loadSupplier.get().forEach(cache::put);
    }

    public void clearCache() {
        cache.invalidateAll();
    }

    public Function<K, V> of(Function<K, V> fn) {
        return key -> Optional.ofNullable(key)
                .map(k ->
                        Optional.of(key)
                                .map(cache::getIfPresent)
                                .orElseGet(() -> {
                                    V applied = fn.apply(key);
                                    cache.put(key, applied);
                                    return applied;
                                })
                )
                .orElseThrow(() -> new IllegalArgumentException("key is null"));//TODO: create custom exception
    }
}