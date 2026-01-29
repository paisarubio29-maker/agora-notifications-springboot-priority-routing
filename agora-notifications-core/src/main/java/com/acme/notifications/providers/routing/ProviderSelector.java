package com.acme.notifications.providers.routing;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public final class ProviderSelector<M, P> {

    private final Map<String, P> providers;
    private final String defaultProviderKey;
    private final Function<M, String> keyFn;

    public ProviderSelector(Map<String, P> providers, String defaultProviderKey, Function<M, String> keyFn) {
        this.providers = Objects.requireNonNull(providers);
        this.defaultProviderKey = Objects.requireNonNull(defaultProviderKey);
        this.keyFn = Objects.requireNonNull(keyFn);

        if (!providers.containsKey(defaultProviderKey)) {
            throw new IllegalArgumentException("Default provider key not found: " + defaultProviderKey);
        }
    }

    public P select(M message) {
        String key = keyFn.apply(message);
        if (key == null || key.isBlank()) key = defaultProviderKey;
        return providers.getOrDefault(key.toUpperCase(), providers.get(defaultProviderKey));
    }
}
