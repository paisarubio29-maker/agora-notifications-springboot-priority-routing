package com.acme.notifications.providers.routing;

import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.providers.SmsProvider;

import java.util.Map;

public final class PriorityRoutingSmsProvider implements SmsProvider {

    private final ProviderSelector<SmsMessage, SmsProvider> selector;

    public PriorityRoutingSmsProvider(Map<String, SmsProvider> providers, String defaultKey, PriorityRules rules) {
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
    public com.acme.notifications.result.SendResult deliver(SmsMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "PriorityRoutingSmsProvider";
    }

    private static Map<String, SmsProvider> normalizeKeys(Map<String, SmsProvider> providers) {
        return providers.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
