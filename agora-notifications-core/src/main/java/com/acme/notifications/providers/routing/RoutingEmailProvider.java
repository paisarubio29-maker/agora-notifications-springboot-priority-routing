package com.acme.notifications.providers.routing;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.providers.EmailProvider;
import com.acme.notifications.result.SendResult;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RoutingEmailProvider implements EmailProvider {

    private final ProviderSelector<EmailMessage, EmailProvider> selector;

    public RoutingEmailProvider(Map<String, EmailProvider> providers, String defaultKey) {
        Objects.requireNonNull(providers);

        Map<String, EmailProvider> normalized = normalizeKeys(providers);

        this.selector = new ProviderSelector<>(
                normalized,
                defaultKey.toUpperCase(),
                (EmailMessage msg) -> {
                    var meta = msg.metadata();
                    if (meta == null) return null;
                    return meta.get(ProviderKey.METADATA_KEY);
                }
        );
    }

    @Override
    public SendResult deliver(EmailMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "RoutingEmailProvider";
    }

    private static Map<String, EmailProvider> normalizeKeys(Map<String, EmailProvider> providers) {
        return providers.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
