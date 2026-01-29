package com.acme.notifications.apiapp.web.dto;

import com.acme.notifications.model.NotificationPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record NotificationRequest(
        @NotNull NotificationType type,
        @NotBlank String to,
        String subject,
        String title,
        @NotBlank String body,
        @NotNull NotificationPriority priority,
        String provider,
        Map<String, String> metadata
) {
}
