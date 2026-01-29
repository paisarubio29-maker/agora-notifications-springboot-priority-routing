package com.acme.notifications.providers.routing;

import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.providers.SmsProvider;
import com.acme.notifications.result.SendResult;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RoutingSmsProvider implements SmsProvider {

    private final ProviderSelector<SmsMessage, SmsProvider> selector;

    public RoutingSmsProvider(Map<String, SmsProvider> providers, String defaultKey) {
        Objects.requireNonNull(providers);

        Map<String, SmsProvider> normalized = normalizeKeys(providers);

        this.selector = new ProviderSelector<>(
                normalized,
                defaultKey.toUpperCase(),
                (SmsMessage msg) -> {
                    var meta = msg.metadata();
                    if (meta == null) return null;
                    return meta.get(ProviderKey.METADATA_KEY);
                }
        );
    }

    @Override
    public SendResult deliver(SmsMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "RoutingSmsProvider";
    }

    private static Map<String, SmsProvider> normalizeKeys(Map<String, SmsProvider> providers) {
        return providers.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
