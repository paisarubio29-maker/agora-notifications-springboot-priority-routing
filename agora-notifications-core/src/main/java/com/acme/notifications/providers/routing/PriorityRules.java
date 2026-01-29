package com.acme.notifications.providers.routing;

import com.acme.notifications.model.NotificationPriority;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mapping from NotificationPriority -> provider key (e.g., SENDGRID, MAILGUN).
 */
public final class PriorityRules {
    private final EnumMap<NotificationPriority, String> rules;

    public PriorityRules(Map<NotificationPriority, String> rules) {
        Objects.requireNonNull(rules);
        this.rules = new EnumMap<>(NotificationPriority.class);
        this.rules.putAll(rules);
    }

    public String providerFor(NotificationPriority priority) {
        if (priority == null) return null;
        return rules.get(priority);
    }
}
