package com.acme.notifications.model;

import java.util.Map;

public record SmsMessage(
        String to,
        String body,
        NotificationPriority priority,
        Map<String, String> metadata
) implements NotificationMessage {
}
