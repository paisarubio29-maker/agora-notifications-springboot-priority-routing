package com.acme.notifications.model;

import java.util.Map;

public record PushMessage(
        String to,
        String title,
        String body,
        NotificationPriority priority,
        Map<String, String> metadata
) implements NotificationMessage {
}
