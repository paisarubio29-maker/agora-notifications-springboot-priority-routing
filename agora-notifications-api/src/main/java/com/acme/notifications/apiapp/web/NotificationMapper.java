package com.acme.notifications.apiapp.web;

import com.acme.notifications.apiapp.web.dto.NotificationRequest;
import com.acme.notifications.model.EmailMessage;
import com.acme.notifications.model.NotificationMessage;
import com.acme.notifications.model.PushMessage;
import com.acme.notifications.model.SmsMessage;
import com.acme.notifications.providers.routing.ProviderKey;

import java.util.Map;

public final class NotificationMapper {
    private NotificationMapper() {
    }

    public static NotificationMessage toDomain(NotificationRequest r) {
        var meta = r.metadata() == null ? Map.<String, String>of() : r.metadata();
        if (r.provider() != null && !r.provider().isBlank()) {
            meta = new java.util.HashMap<>(meta);
            meta.put(ProviderKey.METADATA_KEY, r.provider().toUpperCase());
        }
        return switch (r.type()) {
            case EMAIL -> new EmailMessage(r.to(), r.subject(), r.body(), r.priority(), meta);
            case SMS -> new SmsMessage(r.to(), r.body(), r.priority(), meta);
            case PUSH -> new PushMessage(r.to(), r.title(), r.body(), r.priority(), meta);
        };
    }
}
