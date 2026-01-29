package com.acme.notifications.providers.routing;

import com.acme.notifications.model.PushMessage;
import com.acme.notifications.providers.PushProvider;

import java.util.Map;

public final class PriorityRoutingPushProvider implements PushProvider {

    private final ProviderSelector<PushMessage, PushProvider> selector;

    public PriorityRoutingPushProvider(Map<String, PushProvider> providers, String defaultKey, PriorityRules rules) {
        this.selector = new ProviderSelector<>(
                normalizeKeys(providers),
                defaultKey.toUpperCase(),
                msg -> {
                    var meta = msg.metadata();
                    if (meta != null) {
                        var override = meta.get(ProviderKey.METADATA_KEY);
                        if (override != null && !override.isBlank()) return override;
                    }
                    return rules.providerFor(msg.priority());
                }
        );
    }

    @Override
    public com.acme.notifications.result.SendResult deliver(PushMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "PriorityRoutingPushProvider";
    }

    private static Map<String, PushProvider> normalizeKeys(Map<String, PushProvider> providers) {
        return providers.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
