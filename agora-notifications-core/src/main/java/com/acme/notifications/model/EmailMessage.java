package com.acme.notifications.model;

import java.util.Map;

public record EmailMessage(
        String to,
        String subject,
        String body,
        NotificationPriority priority,
        Map<String, String> metadata
) implements NotificationMessage {
}
