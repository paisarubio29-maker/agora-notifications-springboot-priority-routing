package com.acme.notifications.providers.routing;

import com.acme.notifications.model.PushMessage;
import com.acme.notifications.providers.PushProvider;
import com.acme.notifications.result.SendResult;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RoutingPushProvider implements PushProvider {

    private final ProviderSelector<PushMessage, PushProvider> selector;

    public RoutingPushProvider(Map<String, PushProvider> providers, String defaultKey) {
        Objects.requireNonNull(providers);

        Map<String, PushProvider> normalized = normalizeKeys(providers);

        this.selector = new ProviderSelector<>(
                normalized,
                defaultKey.toUpperCase(),
                (PushMessage msg) -> {
                    var meta = msg.metadata();
                    if (meta == null) return null;
                    return meta.get(ProviderKey.METADATA_KEY);
                }
        );
    }

    @Override
    public SendResult deliver(PushMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "RoutingPushProvider";
    }

    private static Map<String, PushProvider> normalizeKeys(Map<String, PushProvider> providers) {
        return providers.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
