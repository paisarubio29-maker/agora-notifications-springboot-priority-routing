package com.acme.notifications.model;

import java.util.Map;

public sealed interface NotificationMessage permits EmailMessage, SmsMessage, PushMessage {
    String to();

    String body();

    NotificationPriority priority();

    Map<String, String> metadata();
}
