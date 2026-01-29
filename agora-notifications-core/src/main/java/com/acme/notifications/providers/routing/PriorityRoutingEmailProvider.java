package com.acme.notifications.providers.routing;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.providers.EmailProvider;
import com.acme.notifications.result.SendResult;

import java.util.Map;
import java.util.Objects;

import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.providers.EmailProvider;

import java.util.Map;

public final class PriorityRoutingEmailProvider implements EmailProvider {

    private final ProviderSelector<EmailMessage, EmailProvider> selector;

    public PriorityRoutingEmailProvider(
            Map<String, EmailProvider> providers,
            String defaultKey,
            PriorityRules rules
    ) {
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
    public com.acme.notifications.result.SendResult deliver(EmailMessage msg) {
        return selector.select(msg).deliver(msg);
    }

    @Override
    public String name() {
        return "PriorityRoutingEmailProvider";
    }

    private static Map<String, EmailProvider> normalizeKeys(Map<String, EmailProvider> providers) {
        return providers.entrySet().stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        e -> e.getKey().toUpperCase(),
                        Map.Entry::getValue
                ));
    }
}
